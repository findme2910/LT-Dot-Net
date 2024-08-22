using dotnet_backend.DTO;
using dotnet_backend.Model;
namespace dotnet_backend.mapper{
public static class UserMapper
{
    public static UserInformationDTO EntityToUserInformationDTO(User user)
    {
        return new UserInformationDTO
        {
            userId = user.Id,
            avatar = Convert.ToBase64String(user.Avatar),
            name = user.Name
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

    public static FriendViewRequestDTO EntityToFriendRequestDTO(FriendRequest friendRequest)
    {
        return new FriendViewRequestDTO
        {
            userId = friendRequest.FromUserId,
            name = friendRequest.FromUser.Name,
            time = DateToMillis(friendRequest.CreateAt),
            avatar = Convert.ToBase64String(friendRequest.FromUser.Avatar)
        };
    }
    public static string EnumToString(NotificationType type)
{
    if (type != null)
    {
        return type.ToString();
    }
    return null;
}

    public static NotificationDTO EntityToNotificationDTO(Notification notification)
{
    return new NotificationDTO
    {
        avatar = Convert.ToBase64String(notification.Trigger.Avatar),
        content = notification.Content,
        userId = notification.Trigger.Id,  // Sử dụng Trigger.Id vì bạn không có TriggerId trong Notification
        name = notification.Trigger?.Name, // Lấy tên người kích hoạt thông báo
        postId = notification.post?.Id, // Post có thể là null, do đó cần kiểm tra trước khi lấy Id
        createAt = UserMapper.DateToMillis(notification.CreateAt),
        type = UserMapper.EnumToString(notification.Type)
    };
}
}}
