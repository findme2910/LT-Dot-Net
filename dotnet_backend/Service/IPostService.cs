using dotnet_backend.DTO;
using dotnet_backend.Model;
using System.Collections.Generic;

namespace dotnet_backend.Service
{// Chỉnh sửa lại một chút tham số của get và save là có thêm userID để lấy ra userID hiện tại thôi không cần phải lấy hết user
    public interface IPostService
    {
        void Save(AddPostDTO dto,int userId);
        //lấy ra bài viết người dùng hiện tại đang đăng nhập
        List<Post> Get(int userId);
        // láy ra tất cả bài viết của người dùng với id nào đó
        List<Post> GetSpecific(int userId);
        //lấy ra danh sách comment trên một bài post nào đó
        List<Comment> GetComments(int postId);
    }
}
