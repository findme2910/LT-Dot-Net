using Microsoft.AspNetCore.Mvc;
using System.Linq;
using dotnet_backend.DTO;
using dotnet_backend.mapper;
using dotnet_backend.Helper;
using Microsoft.AspNetCore.Authorization;

namespace dotnet_backend.Controllers
{
    [ApiController]
    [Route("noti")]
public class NotificationController : ControllerBase
{
    private readonly NotificationService _notificationService;
    private readonly IConfiguration _configuration;
    private readonly JwtHelper _jwtHelper;

    public NotificationController(NotificationService notificationService, IConfiguration configuration, JwtHelper jwtHelper)
    {
        _notificationService = notificationService;
        _configuration = configuration;
        _jwtHelper = jwtHelper;
    }

    [HttpGet]
    [Authorize]
    public IActionResult GetNotifications([FromQuery] RequestNotificationDTO dto)
    {
        try
        {
            var notifications = _notificationService.GetNotis(dto)
                .Select(UserMapper.EntityToNotificationDTO)
                .ToList();
            return Ok(notifications);
        }
        catch (Exception e)
        {
            return BadRequest(new { Message = e.Message });
        }
    }
}
}
