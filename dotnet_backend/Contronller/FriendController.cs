using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.mapper;
using dotnet_backend.Service;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using WebNet.Data;

[Route("friend")]
[ApiController]
    public class FriendsController : ControllerBase
{
    IConfiguration configuration;
    JwtHelper jwtHelper;
    private readonly IUserService _userService;
    private readonly AuthStaticService _authStaticService;
    ApplicationDBContext applicationDBContext;

    public FriendsController(IConfiguration configuration, JwtHelper jwtHelper, IUserService userService, ApplicationDBContext applicationDBContext, AuthStaticService authStaticService)
    {
        this.applicationDBContext = applicationDBContext;
        this.configuration = configuration;
        this.jwtHelper = jwtHelper;
        _userService = userService;
        _authStaticService = authStaticService;
    }
    //Phương thức lấy ra danh sách bạn bè của người dùng
    [HttpGet]
    [Authorize]
public IActionResult GetFriends()
{
    try
    {
        var currentUser = _authStaticService.CurrentUser();
        if (currentUser == null)
        {
            return BadRequest(new ResponseDTO { response = "User not found" });
        }

        if (currentUser.Friends == null)
        {
            return BadRequest(new ResponseDTO { response = "Friends list is null" });
        }

        // Lọc ra bạn bè, loại bỏ chính người dùng hiện tại
        var friends = currentUser.Friends
            .Where(f => f.Id != currentUser.Id)
            .Select(UserMapper.EntityToFriendViewDTO)
            .ToList();

        return Ok(friends);
    }
    catch (Exception e)
    {
        return BadRequest(new ResponseDTO { response = e.Message });
    }
}


    //Phương thức gợi ý danh sách bạn bè
    [HttpGet("suggest")]
    [Authorize]
    public IActionResult GetSuggestAddFriendList()
    {
        try
        {
            var suggestedFriends = _userService.GetSuggestAddFriend()
                .Select(UserMapper.EntityToFriendViewDTO)
                .ToList();
            return Ok(suggestedFriends);
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }
    //Phương thức addfriend người khác 
    [HttpPost("addfriend")]
    [Authorize]
    public IActionResult AddFriend([FromBody] FriendRequestDTO dto)
    {
        try
        {
            _userService.HandleFriendRequest(dto);
            return Ok(new ResponseDTO { response = "Success" });
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }

    [HttpPost("reject/addfriend")]
    [Authorize]
    public IActionResult RejectAddFriend([FromBody] FriendRequestDTO dto)
    {
        try
        {
            _userService.HandleRejectFriendRequest(dto);
            return Ok(new ResponseDTO { response = "Success" });
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }

    [HttpPost("accept/addfriend")]
    [Authorize]
    public IActionResult AcceptAddFriend([FromBody] FriendRequestDTO dto)
    {
        try
        {
            _userService.HandleAcceptFriendRequest(dto);
            return Ok(new ResponseDTO { response = "Success" });
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }

    [HttpPost("cancel/addfriend")]
    [Authorize]
    public IActionResult CancelAddFriend([FromBody] FriendRequestDTO dto)
    {
        try
        {
            _userService.HandleCancelFriendRequest(dto);
            return Ok(new ResponseDTO { response = "Success" });
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }
    //phương thức lấy ra danh sách những người bạn đang addfriend mình
    [HttpGet("addfriend")]
    [Authorize]
    public IActionResult GetAddFriends()
    {
        try
        {
            var friendRequests = _userService.GetAllFriendRequest()
                .Select(UserMapper.EntityToFriendRequestDTO)
                .ToList();
            return Ok(friendRequests);
        }
        catch (Exception e)
        {
            return BadRequest(new ResponseDTO { response = e.Message });
        }
    }
}
