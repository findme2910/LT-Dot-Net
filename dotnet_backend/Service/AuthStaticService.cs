using dotnet_backend.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using System.Linq;
using WebNet.Data;

public class AuthStaticService
{
    private readonly IHttpContextAccessor _httpContextAccessor;
    private readonly ApplicationDBContext _context;

    public AuthStaticService(IHttpContextAccessor httpContextAccessor, ApplicationDBContext context)
    {
        _httpContextAccessor = httpContextAccessor;
        _context = context;
    }
    //láy thông tin người dùng hiện tại đang đăng nhập
    public User CurrentUser()
{
    var userIdValue = _httpContextAccessor.HttpContext.Items["UserId"];
    if (userIdValue == null)
    {
        throw new ArgumentNullException("UserId", "UserId cannot be null.");
    }

    var userIdString = userIdValue.ToString();
    if (string.IsNullOrEmpty(userIdString))
    {
        throw new ArgumentNullException("UserId", "UserId cannot be null or empty.");
    }

    var userId = int.Parse(userIdString);

    return _context.Users
        .Include(u => u.Friends) // Nạp danh sách bạn bè
        .FirstOrDefault(u => u.Id == userId);
}




}
