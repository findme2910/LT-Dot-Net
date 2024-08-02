using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Model
{
    public class Comment
    {

        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public int UserId { get; set; }
        public int PostId { get; set; }

        public string Content { get; set; }

        // Entity Framework sẽ tự động gán giá trị cho các thuộc tính thời gian
        public DateTime CreateAt { get; set; }
        public DateTime UpdateAt { get; set; }

        // Navigation properties
        public User User { get; set; }
        public Post Post { get; set; }

        // Đối với các reply, không nên sử dụng ElementCollection vì đây là quan hệ nhiều-nhiều với chính nó
        public ICollection<Comment> Replys { get; set; } = new List<Comment>();
    }
}