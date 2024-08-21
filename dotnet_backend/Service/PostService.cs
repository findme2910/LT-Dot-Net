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

        public PostService(IConfiguration configuration, ApplicationDBContext context, JwtHelper jwtHelper)
        {
            _configuration = configuration;
            _context = context;
            _jwtHelper = jwtHelper;
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
            return _context.Posts
                .Where(p => users.Contains(p.User))
                .Include(p => p.Comments)
                .Include(p => p.Likes)
                .OrderByDescending(p => p.CreateAt)
                .ToList();
        }

        public List<Post> GetSpecific(int userId)
        {
            return _context.Posts
                .Where(p => p.UserId == userId)
                .Include(p => p.Comments)
                .Include(p => p.Likes)
                .Include(p => p.User)
                .OrderByDescending(p => p.CreateAt)
                .ToList();
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



    }
}
