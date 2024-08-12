namespace dotnet_backend.DTO{
public class CommentViewDTO
{
    public int commentId { get; set; }
	public string avatar { get; set; }
	public string name { get; set; }
	public string content { get; set; }
	public long createAt { get; set; }
	public List<CommentViewDTO> replys { get; set; }
}}