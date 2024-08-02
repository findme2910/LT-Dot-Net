using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using dotnet_backend.Model;

[Table("FriendRequests")]
public class FriendRequest
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int Id { get; set; }
    public int UserId { get; set; }

    // Navigation property
    [ForeignKey("FromUserId")]
    public User FromUser { get; set; }

    public DateTime CreateAt { get; set; }
}