using System;
using System.Collections.Generic;
using System.Linq;
using dotnet_backend.DTO;
using dotnet_backend.Model;
using WebNet.Data;
using Microsoft.EntityFrameworkCore;
using dotnet_backend.Helper;

namespace dotnet_backend.Service
{
    public class PostService : IPostService
    {
        private readonly IConfiguration _configuration;
        private readonly ApplicationDBContext _context;
        private readonly JwtHelper _jwtHelper;
        private readonly AuthStaticService _authStaticService;

        public PostService(IConfiguration configuration, ApplicationDBContext context, AuthStaticService authStaticService, JwtHelper jwtHelper)
        {
            _configuration = configuration;
            _context = context;
            _jwtHelper = jwtHelper;
            _authStaticService = authStaticService;
        }

        public void Save(AddPostDTO dto, int userId)
        {
            var user = _context.Users.FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            var post = new Post
            {
                User = user,
                Content = dto.content,
                Comments = new List<Comment>(),
                Likes = new List<Like>(),
                Image = Convert.FromBase64String(dto.image),
                CreateAt = DateTime.UtcNow,
                UpdateAt = DateTime.UtcNow
            };

            _context.Posts.Add(post);
            _context.SaveChanges();
        }

        public List<Post> Get(int userId)
        {
            var user = _context.Users.Include(u => u.Friends).FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                throw new Exception("User not found");
            }

            var users = new List<User>(user.Friends) { user };
            var posts = _context.Posts
                .Where(p => users.Contains(p.User))
                .Include(p => p.Comments)
                .Include(p => p.Likes)
                .OrderByDescending(p => p.CreateAt)
                .ToList();
            return filterByPostScope(posts);
        }

        public List<Post> GetSpecific(int userId)
        {
            var posts = _context.Posts
                .Where(p => p.UserId == userId)
                .Include(p => p.Comments)
                .Include(p => p.Likes)
                .Include(p => p.User)
                .OrderByDescending(p => p.CreateAt)
                .ToList();
            return filterByPostScope(posts);
        }

        public List<Comment> GetComments(int postId)
        {
            var post = _context.Posts
                .Include(p => p.Comments)
                    .ThenInclude(c => c.User)  // Bao gồm thông tin User của Comment
                .FirstOrDefault(p => p.Id == postId);
            var replyIds = post?.Comments.SelectMany(c => c.Replys.Select(r => r.Id)).ToList();

            var mainComments = post?.Comments
    .Where(c => !replyIds.Contains(c.Id))
    .ToList();

            if (post == null)
            {
                throw new Exception("Post not found");
            }

            return mainComments;
        }

        public void changeScope(int id, PostScope scope)
        {
            var post = _context.Posts.Find(id);
            var user = _authStaticService.CurrentUser();
            if (post == null)
            {
                throw new Exception("Post id not found");
            }
            if (post.UserId != user.Id)
            {
                throw new Exception("This Post didn't owned by you");
            }
            post.PostScope = scope;
            _context.SaveChanges();
        }

        private List<Post> filterByPostScope(List<Post> posts)
        {
            var currentUserId = _authStaticService.CurrentUser().Id;

            var listFriendID = _authStaticService.CurrentUser().Friends.Select(n => n.Id).ToList();

            var filterPosts = posts.Where(post =>
                post.PostScope != PostScope.PRIVATE && post.PostScope != PostScope.FRIEND ||
                (post.PostScope != PostScope.PRIVATE || post.UserId == currentUserId) ||
                (post.PostScope == PostScope.FRIEND && (post.UserId == currentUserId || listFriendID.Contains(post.UserId)))
            ).ToList();

            return filterPosts;

        }
    }
}
