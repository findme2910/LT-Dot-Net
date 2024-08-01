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

        // Navigation properties
        public User Trigger { get; set; }
       
        public DateTime CreateAt { get; set; }

        // Enum mapping
        public NotificationType Type { get; set; }
    }
}