using dotnet_backend.Model;

public class NotificationDTO
{
    public string avatar { get; set; }
    public string content { get; set; }
    public int triggerId { get; set; }
    public string triggerName { get; set; }
    public int? postId { get; set; } // Nullable vì Post có thể là null
    public long createAt { get; set; }
    public NotificationType type { get; set; }
}
