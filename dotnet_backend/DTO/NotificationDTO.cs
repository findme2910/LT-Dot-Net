using dotnet_backend.Model;

public class NotificationDTO
{
    public string avatar { get; set; }
    public string content { get; set; }
    public int userId { get; set; }
    public string name { get; set; }
    public int? postId { get; set; } // Nullable vì Post có thể là null
    public long createAt { get; set; }
    public string type { get; set; }
    public bool active {get;set;}
}
