namespace dotnet_backend.DTO{
public class ProfileDTO
{
	public int userId{ get; set; }
	public string name { get; set; }
	public long birth { get; set; }
	public string avatar { get; set; }
	public bool Own { get; set; }
	public List<FriendViewDTO> friends { get; set; }
}}