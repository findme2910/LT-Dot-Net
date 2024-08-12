namespace dotnet_backend.DTO{
public class PostViewDTO
{
	public int userId { get; set; }
	public int postId { get; set; }
	public string name { get; set; }
	public string avatarUser { get; set; }
	public string image { get; set; }
	public string content { get; set; }
	// thuộc tính này thể hiện người dùng đã like hay chưa like cái Post này hay không
	public bool isLiked { get; set; }
	public int numberLike { get; set; }
	public int numberComment { get; set; }
	public long createAt { get; set; }
}}