using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Model
{
    public class Post
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public int UserId { get; set; }

        public string Content { get; set; }

        // Blob trong Java chuyển thành byte[] trong C#
        public byte[] Image { get; set; }

        // Entity Framework sẽ tự động gán giá trị cho các thuộc tính thời gian
        public DateTime CreateAt { get; set; }
        public DateTime UpdateAt { get; set; }

        // Navigation properties
        public User User { get; set; }

        public ICollection<Like> Likes { get; set; } = new List<Like>();
        public ICollection<Comment> Comments { get; set; } = new List<Comment>();
    }
}