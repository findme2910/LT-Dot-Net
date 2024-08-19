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
    public int FromUserId { get; set; } // Khóa ngoại cho FromUser
    public int UserId { get; set; }  // Đây là UserId của người nhận yêu cầu kết bạn

    [ForeignKey("FromUserId")]
    public User FromUser { get; set; } // Người gửi yêu cầu kết bạn

    public DateTime CreateAt { get; set; }
}
