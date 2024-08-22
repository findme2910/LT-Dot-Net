using dotnet_backend.DTO;
using dotnet_backend.Model;
using Microsoft.EntityFrameworkCore;
using WebNet.Data;

public class NotificationService
{
    private readonly ApplicationDBContext _context;
    private readonly AuthStaticService _authStaticService;

    public NotificationService(ApplicationDBContext context, AuthStaticService authStaticService)
    {
        _context = context;
        _authStaticService = authStaticService;
    }
    //ok
    public void LikeNoti(Post to)
    {
        var currUser = _authStaticService.CurrentUser();
        var notification = new Notification
        {
            Type = NotificationType.LIKE_POST,
            Trigger = currUser,
            Content = "Đã thích bài viết của bạn",
            post = to,
            UserId = to.UserId,
            CreateAt = DateTime.UtcNow.AddHours(7),
        };
        _context.Notifications.Add(notification);
        _context.SaveChanges();
    }
    //ok
    public void ReplyNoti(Comment to)
    {
        var currUser = _authStaticService.CurrentUser();
        var notification = new Notification
        
        {
            Type = NotificationType.REPLY_COMMENT,
            Trigger = currUser,
            Content = "Đã phản hồi bình luận của bạn",
            post = to.Post,
            UserId = to.UserId,
            CreateAt = DateTime.UtcNow.AddHours(7),
        };

        to.User.Notifications.Add(notification);
        _context.Notifications.Add(notification);
        _context.SaveChanges();
    }
    //ok
    public void CommentNoti(Post to)
    {
        var currUser = _authStaticService.CurrentUser();
        var notification = new Notification
        {
            Type = NotificationType.COMMENT_POST,
            Trigger = currUser,
            Content = "Đã bình luận bài viết của bạn",
            post = to,
            UserId = to.UserId,
            CreateAt = DateTime.UtcNow.AddHours(7),
        };
        _context.Notifications.Add(notification);
        _context.SaveChanges();
    }
    //ok
    public void RequestAddFriend(User to)
    {
        var currUser = _authStaticService.CurrentUser();
        var notification = new Notification
        {
            Type = NotificationType.REQUEST_ADD_FRIEND,
            Trigger = currUser,
            Content = "Đã gửi yêu cầu kết bạn",
            CreateAt = DateTime.UtcNow.AddHours(7),
        };

        to.Notifications.Add(notification);
        _context.Notifications.Add(notification);
        _context.SaveChanges();
    }
    //ok
    public void AcceptionAddFriend(User to)
    {
        var currUser = _authStaticService.CurrentUser();
        var notification = new Notification
        {
            Type = NotificationType.ACCEPT_ADD_FRIEND,
            Trigger = currUser,
            Content = "Đã chấp nhận lời mời kết bạn",
            CreateAt = DateTime.UtcNow.AddHours(7),
        };

        to.Notifications.Add(notification);
        _context.Notifications.Add(notification);
        _context.SaveChanges();
    }
    //get ok
    public List<Notification> GetNotis(RequestNotificationDTO dto)
    {
        var currUser = _authStaticService.CurrentUser();
       var notifications = _context.Notifications
    .Include(n => n.Trigger)  // Tải thông tin về Trigger (người gửi thông báo)
    .Include(n => n.post)      // Tải thông tin về Post (bài viết liên quan đến thông báo)
    .Where(n => n.UserId == currUser.Id)  // Lọc theo UserId
    .ToList();


        currUser.CurrentNoti = notifications.Count;
        _context.SaveChanges();

        if (dto.next * 10 >= notifications.Count)
            return new List<Notification>();

        return notifications
            .Skip(dto.next * 10)
            .Take(10)
            .ToList();
    }
}
