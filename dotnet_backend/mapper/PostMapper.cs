using dotnet_backend.DTO;
using dotnet_backend.Model;
using System.Collections.Generic;
using System.Linq;

namespace dotnet_backend.Helper
{
    public static class PostMapper
    {
        public static PostViewDTO EntityToViewPostDTO(Post post)
        {
            return new PostViewDTO
            {
                postId = post.Id,
                userId = post.User.Id,
                avatarUser = Convert.ToBase64String(post.User.Avatar),
                image = Convert.ToBase64String(post.Image),
                content = post.Content,
                numberLike = post.Likes.Count,
                postScope = post.PostScope,
                numberComment = post.Comments.Count ,
                createAt = DateToMillis(post.CreateAt),
                name = post.User.Name
            };
        }
         public static long DateToMillis(DateTime date)
    {
        return ((DateTimeOffset)date).ToUnixTimeMilliseconds();
    }

        public static CommentViewDTO EntityToCommentViewDTO(Comment comment)
        {
            return new CommentViewDTO
            {
                commentId = comment.Id,
                avatar = Convert.ToBase64String(comment.User.Avatar),
                name = comment.User.Name,
                content = comment.Content,
                createAt = DateToMillis(comment.CreateAt),
                replys = comment.Replys.Select(reply => new CommentViewDTO
                {
                    commentId = reply.Id,
                    avatar = Convert.ToBase64String(reply.User.Avatar),
                    name = reply.User.Name,
                    content = reply.Content,
                    createAt = DateToMillis(reply.CreateAt)
                }).ToList()
            };
        }
    }
}
