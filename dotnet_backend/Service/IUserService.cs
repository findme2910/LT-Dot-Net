using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using dotnet_backend.DTO;
using dotnet_backend.Model;

namespace dotnet_backend.Service
{// cái nào cần userId thôi thì truyền userId rồi qua controller truyền lại userID hiện tại đang đăng nhập
    public interface IUserService
    {
        void register(RegisterDTO dto);
        string login(LoginDTO dto);
        void Like(LikeDTO dto, int userId);
        void Comment(CommentDTO dto, int userId);
        HomeViewDTO GetHomeView(int userId);
        UserInformationDTO GetUserInfo(int userId);
        void UpdateUser(UserUpdateDTO dto, int userId);
        //bên phần profile
        ProfileDTO GetProfile(int userId);
        void UpdateAvatar(UpdateAvatarDTO dto, int userId);
        //bên phần post 
        void ReplyComment(ReplyCommentDTO dto, int userId);
        //phần friends
        void HandleFriendRequest(FriendRequestDTO dto);
        void HandleRejectFriendRequest(FriendRequestDTO dto);
        void HandleAcceptFriendRequest(FriendRequestDTO dto);
        void HandleCancelFriendRequest(FriendRequestDTO dto);
        List<User> GetSuggestAddFriend();
        List<FriendRequest> GetAllFriendRequest();
        List<User> FindAllAcceptFriend();
    }
}