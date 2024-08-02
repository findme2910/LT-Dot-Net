using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Model
{
    public class User
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        public string Name { get; set; }

        // Enum tương ứng với Gender của Java
        public Gender Gender { get; set; }

        // Validation attributes nếu cần
        [RegularExpression(@"(\+?84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}", ErrorMessage = "Invalid phone number")]
        public string Phone { get; set; }

        public DateTime Birth { get; set; }

        // Blob trong Java chuyển thành byte[] trong C#
        public byte[] Avatar { get; set; }

        // Validation attributes nếu cần
        [RegularExpression(@"^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\\-={}\\[\\]|;:'""<>,./?])(?=.*\\d).{6,}$", ErrorMessage = "Invalid password")]
        [MinLength(6)]
        public string Password { get; set; }

        // Navigation properties
        public ICollection<Comment> Comments { get; set; } = new List<Comment>();
        public ICollection<Post> Posts { get; set; } = new List<Post>();
        public ICollection<User> Friends { get; set; } = new List<User>();
        public ICollection<FriendRequest> FriendRequests { get; set; } = new List<FriendRequest>();
        public ICollection<Notification> Notifications { get; set; } = new List<Notification>();

        public int CurrentNoti { get; set; }
    }
}