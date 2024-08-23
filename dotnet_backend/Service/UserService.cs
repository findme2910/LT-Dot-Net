using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using dotnet_backend.CustomException;
using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.mapper;
using dotnet_backend.Model;
using Microsoft.EntityFrameworkCore;
using WebNet.Data;

namespace dotnet_backend.Service
{
    public class UserService : IUserService
    {
        private readonly PasswordService _passwordHasher; // Interface cho mã hóa mật khẩu
        private readonly IConfiguration _configuration;
        private readonly ApplicationDBContext _context;
        private readonly AuthStaticService _authStaticService;
        private readonly NotificationService _notificationService;

        private readonly JwtHelper jwtHelper;
        public UserService(
    PasswordService passwordService,
    JwtHelper jwtHelper,
    IConfiguration configuration,
    ApplicationDBContext applicationDBContext,
    AuthStaticService authStaticService,
    NotificationService notificationService) // Thêm AuthStaticService vào constructor
        {
            _passwordHasher = passwordService;
            _configuration = configuration;
            _context = applicationDBContext;
            this.jwtHelper = jwtHelper;
            _authStaticService = authStaticService; // Khởi tạo AuthStaticService
            _notificationService = notificationService; // Khởi tạo NotificationService
        }
        public void register(RegisterDTO dto)
        {
            if (_context.Users.Any(u => u.Phone == dto.phone))
            {
                throw new UserAlreadyExistsException("Phone number already exists.");
            }
            if (!Enum.TryParse<Gender>(dto.gender, out var gender))
            {
                throw new InvalidGenderException("Invalid gender provided.");
            }
            var user = new User
            {
                Phone = dto.phone,
                Name = dto.name,
                Password = _passwordHasher.HashPassword(dto.password),
                Comments = new List<Comment>(),
                Posts = new List<Post>(),
                Gender = gender,
                Birth = new DateTime(dto.birth), 
                Avatar = Convert.FromBase64String(_configuration["res:avatar:male"]) // Thay đổi tùy thuộc vào cách xử lý blob
            };

            _context.Users.Add(user);
            _context.SaveChanges();
        }
        public string login(LoginDTO loginDTO)
        {
            var user = _context.Users.First(u => u.Phone == loginDTO.phone);
            if (user == null)
            {
                throw new Exception("Phone number does not exist");
            }

            if (_passwordHasher.VerifyPassword(loginDTO.password, user.Password))
            {
                return jwtHelper.GenerateJwtToken(user.Id);
            }
            else
            {
                throw new Exception("Password incorrect");
            }
        }
        public void Like(LikeDTO dto, int userId)
        {
            var post = _context.Posts.FirstOrDefault(p => p.Id == dto.postId);
            if (post == null)
            {
                throw new Exception("Post not found");
            }

            // Kiểm tra xem người dùng đã like bài viết này chưa
            var existingLike = _context.Likes.FirstOrDefault(l => l.UserId == userId && l.PostId == dto.postId);
            if (existingLike != null)
            {
                // Nếu đã like, bỏ like
                _context.Likes.Remove(existingLike);
            }
            else
            {
                // Nếu chưa like, thêm like
                var like = new Like
                {
                    UserId = userId,
                    PostId = dto.postId,
                    CreateAt = DateTime.UtcNow.AddHours(7),
                };
                _context.Likes.Add(like);
                
                
                _notificationService.LikeNoti(post);
            }

            // Lưu thay đổi vào cơ sở dữ liệu
            _context.SaveChanges();
        }

        //Comment
        public void Comment(CommentDTO dto, int userId)
        {
            var post = _context.Posts.FirstOrDefault(p => p.Id == dto.postId);
            if (post == null)
            {
                throw new Exception("Post not found");
            }

            var comment = new Comment
            {
                UserId = userId,
                PostId = dto.postId,
                Content = dto.content,
                CreateAt = DateTime.UtcNow.AddHours(7),
                UpdateAt = DateTime.UtcNow.AddHours(7),
            };
            _context.Comments.Add(comment);
            _context.SaveChanges();
            //Gửi thông báo
            _notificationService.CommentNoti(comment.Post);
        }


        //getHomeView
        public HomeViewDTO GetHomeView(int userId)
        {
            var user = _context.Users.Include(u => u.FriendRequests).Include(u => u.Notifications).FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            return new HomeViewDTO
            {
                numberAddFriend = user.FriendRequests.Count,
                numberNoti = user.Notifications.Count - user.CurrentNoti
            };
        }

        //GetUserInfo
        public UserInformationDTO GetUserInfo(int userId)
        {
            var user = _context.Users.FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            return UserMapper.EntityToUserInformationDTO(user);
        }


        //Update
        public void UpdateUser(UserUpdateDTO dto, int userId)
        {
            var user = _context.Users.FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            user.Name = dto.name;
            user.Avatar = Convert.FromBase64String(dto.avatar);
            user.Password = _passwordHasher.HashPassword(dto.password);
            user.Birth = new DateTime(dto.birth);

            _context.Users.Update(user);
            _context.SaveChanges();
        }

        // thêm các phương thức liên quan đến profile
        public ProfileDTO GetProfile(int userId)
        {
            var user = _context.Users
                .Include(u => u.Friends)
                .FirstOrDefault(u => u.Id == userId);

            if (user == null)
            {
                throw new Exception("User not found");
            }

            var profileDTO = new ProfileDTO
            {
                userId = user.Id,
                name = user.Name,
                birth = user.Birth.Ticks,
                avatar = Convert.ToBase64String(user.Avatar),
                friends = user.Friends.Select(f => new FriendViewDTO
                {
                    userId = f.Id,
                    name = f.Name,
                    avatar = Convert.ToBase64String(f.Avatar)
                }).ToList(),
                Own = false // Sẽ được cập nhật trong controller
            };

            return profileDTO;
        }

        public void UpdateAvatar(UpdateAvatarDTO dto, int userId)
        {
            var user = _context.Users.FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            user.Avatar = Convert.FromBase64String(dto.avatar);
            _context.Users.Update(user);
            _context.SaveChanges();
        }

        //Thêm các phương thức liên quan đến xác thực post
        public void ReplyComment(ReplyCommentDTO dto, int userId)
        {
            var comment = _context.Comments
                              .Include(c => c.Post)
                              .ThenInclude(p => p.User)
                              .FirstOrDefault(c => c.Id == dto.commentId); // lấy luôn cả post liên quan

            if (comment == null)
            {
                throw new Exception("Comment not found");
            }
            var currentUser = _context.Users.FirstOrDefault(u => u.Id == userId);
            var reply = new Comment
            {
                UserId = userId,
                PostId = comment.PostId,
                Content = dto.content,
                CreateAt = DateTime.UtcNow.AddHours(7),
                UpdateAt = DateTime.UtcNow.AddHours(7),
                User = currentUser
            };
            //Gửi thông báo 

            comment.Replys.Add(reply);
            _context.Comments.Add(reply);
            _context.SaveChanges();
            _notificationService.ReplyNoti(comment); // phần thông báo
        }

        //Phần friend
        public void HandleFriendRequest(FriendRequestDTO dto)
        {
            var currentUser = _authStaticService.CurrentUser();
            var toUser = _context.Users.Find(dto.userId);

            if (toUser == null)
                throw new Exception("User not found");

            if (toUser.Friends.Contains(currentUser))
                throw new Exception("To User is already your friend");

            if (_context.FriendRequests.Any(fr => fr.UserId == dto.userId && fr.FromUser.Id == currentUser.Id))
                throw new Exception("Friend request already sent");

            var friendRequest = new FriendRequest
            {
                FromUserId = currentUser.Id,
                UserId = dto.userId,  // ID của người nhận yêu cầu kết bạn
                FromUser = currentUser, // Người gửi yêu cầu kết bạn
                CreateAt = DateTime.UtcNow.AddHours(7)
            };
            _context.FriendRequests.Add(friendRequest);
            _context.SaveChanges();
            // Gửi thông báo kết bạn
            _notificationService.RequestAddFriend(toUser);  // Gửi thông báo đến người nhận yêu cầu kết bạn
        }


        public void HandleRejectFriendRequest(FriendRequestDTO dto)
        {
            var currentUser = _authStaticService.CurrentUser();
            var friendRequest = _context.FriendRequests
                .FirstOrDefault(fr => fr.UserId == currentUser.Id && fr.FromUser.Id == dto.userId);

            if (friendRequest == null)
                throw new Exception("Friend request not found");

            _context.FriendRequests.Remove(friendRequest);
            _context.SaveChanges();
        }
        //phương thức chấp nhận lời mời kết bạn
        public void HandleAcceptFriendRequest(FriendRequestDTO dto)
        {
            var currentUser = _authStaticService.CurrentUser();
            var friendRequest = _context.FriendRequests
                .FirstOrDefault(fr => fr.UserId == currentUser.Id && fr.FromUser.Id == dto.userId);

            if (friendRequest == null)
                throw new Exception("Friend request not found");

            var fromUser = _context.Users.Find(dto.userId);
            currentUser.Friends.Add(fromUser);
            fromUser.Friends.Add(currentUser);

            _context.FriendRequests.Remove(friendRequest);
            _context.SaveChanges();
            _notificationService.AcceptionAddFriend(fromUser);
        }
        //phương thức xóa người dùng đang là bạn bè khỏi danh sách bạn bè của mình 
        public void HandleCancelFriendRequest(FriendRequestDTO dto)
        {
            var currentUser = _authStaticService.CurrentUser();
            var friend = _context.Users.Find(dto.userId);

            if (friend == null || !currentUser.Friends.Contains(friend))
                throw new Exception("Friend not found");

            currentUser.Friends.Remove(friend);
            friend.Friends.Remove(currentUser);
            _context.SaveChanges();
        }

        public List<User> GetSuggestAddFriend()
        {
            var currentUser = _authStaticService.CurrentUser();
            var friends = currentUser.Friends.Select(f => f.Id).ToList(); // lấy ra danh sách id bạn bè hiện tại
            // Lấy danh sách ID của những người dùng đã gửi yêu cầu kết bạn hoặc nhận được yêu cầu kết bạn
        var pendingRequestIds = _context.FriendRequests
        .Where(fr => (fr.UserId == currentUser.Id || fr.FromUserId == currentUser.Id))
        .Select(fr => fr.UserId == currentUser.Id ? fr.FromUserId : fr.UserId)
        .ToList();
        var excludeIds = friends.Concat(pendingRequestIds).ToList();
             return _context.Users
        .Where(u => !excludeIds.Contains(u.Id) && u.Id != currentUser.Id)
        .ToList();
        }
// lấy ra danh sách các friendrequest
        public List<FriendRequest> GetAllFriendRequest()
{
    var currentUser = _authStaticService.CurrentUser();

    return _context.FriendRequests
                   .Include(fr => fr.FromUser) // Tải đối tượng FromUser đi kèm
                   .Where(fr => fr.UserId == currentUser.Id)
                   .ToList();
}


        public List<User> FindAllAcceptFriend()
        {
            var currentUser = _authStaticService.CurrentUser();
            return _context.Users
                .Where(u => !currentUser.Friends.Contains(u) && u.Id != currentUser.Id)
                .ToList();
        }

    }
}

