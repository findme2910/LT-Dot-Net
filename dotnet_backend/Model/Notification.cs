using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Model
{
    public class Notification
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public int UserId { get; set; }
        public string Content { get; set; }
         public int TriggerId { get; set; }

        // Navigation properties
          [ForeignKey("TriggerId")]
        public User Trigger { get; set; }
       
        public DateTime CreateAt { get; set; }
         public int? PostId { get; set; }  // Nullable nếu thông báo không luôn luôn liên quan đến bài viết
        [ForeignKey("PostId")]
        public Post post {get; set;}
        // Enum mapping
        public NotificationType Type { get; set; }
    }
}