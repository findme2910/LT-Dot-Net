using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Model
{
    public class Like
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public int PostId { get; set; }

        public int UserId { get; set; }

        public DateTime CreateAt { get; set; }

        // Navigation properties
        public User User { get; set; }
        public Post Post { get; set; }
    }
}