using dotnet_backend.Model;

public class NotificationDTO
{
    public string Avatar { get; set; }
    public string Content { get; set; }
    public int TriggerId { get; set; }
    public string TriggerName { get; set; }
    public int? PostId { get; set; } // Nullable vì Post có thể là null
    public DateTime CreateAt { get; set; }
    public NotificationType Type { get; set; }
}
