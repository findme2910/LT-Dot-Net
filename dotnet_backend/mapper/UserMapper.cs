using dotnet_backend.DTO;
using dotnet_backend.Model;
namespace dotnet_backend.mapper{
public static class UserMapper
{
    public static UserInformationDTO EntityToUserInformationDTO(User user)
    {
        return new UserInformationDTO
        {
            UserId = user.Id,
            Avatar = Convert.ToBase64String(user.Avatar),
            Name = user.Name
        };
    }

    public static long DateToMillis(DateTime date)
    {
        return ((DateTimeOffset)date).ToUnixTimeMilliseconds();
    }

    public static List<FriendViewDTO> UsersToFriendViewsDTO(List<User> users)
    {
        return users.Select(user => new FriendViewDTO
        {
            avatar = Convert.ToBase64String(user.Avatar),
            userId = user.Id,
            name = user.Name
        }).Take(5).ToList();
    }
    public static FriendViewDTO EntityToFriendViewDTO(User user)
    {
        return new FriendViewDTO
        {
            userId = user.Id,
            name = user.Name,
            avatar = Convert.ToBase64String(user.Avatar)
        };
    }

    public static FriendRequestDTO EntityToFriendRequestDTO(FriendRequest friendRequest)
    {
        return new FriendRequestDTO
        {
            userId = friendRequest.FromUserId
        };
    }
    public static NotificationDTO EntityToNotificationDTO(Notification notification)
{
    return new NotificationDTO
    {
        Avatar = Convert.ToBase64String(notification.Trigger.Avatar),
        Content = notification.Content,
        TriggerId = notification.Trigger.Id,  // Sử dụng Trigger.Id vì bạn không có TriggerId trong Notification
        TriggerName = notification.Trigger?.Name, // Lấy tên người kích hoạt thông báo
        PostId = notification.post?.Id, // Post có thể là null, do đó cần kiểm tra trước khi lấy Id
        CreateAt = notification.CreateAt,
        Type = notification.Type
    };
}
}}
