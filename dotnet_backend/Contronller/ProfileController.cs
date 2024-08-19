using System;
using System.Linq;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using dotnet_backend.DTO;
using dotnet_backend.Service;
using WebNet.Data;
using dotnet_backend.Helper;
using System.Security.Claims;

namespace dotnet_backend.Controllers
{
    [Route("profile")]
    [ApiController]
    public class ProfileController : ControllerBase
    {
        private readonly ApplicationDBContext applicationDBContext;
        IConfiguration configuration;
        JwtHelper jwtHelper;
        private readonly IUserService _userService;

        public ProfileController(IConfiguration configuration, JwtHelper jwtHelper, IUserService userService, ApplicationDBContext applicationDBContext)
        {
            this.applicationDBContext = applicationDBContext;
            this.configuration = configuration;
            this.jwtHelper = jwtHelper;
            _userService = userService;
        }

        [HttpGet]
        [Authorize]
        public IActionResult GetProfile()
        {
            var userId = GetCurrentUserId();
            var profileDTO = _userService.GetProfile(userId);
            profileDTO.own = true;  // Mark profile as owned by the current user
            return Ok(profileDTO);
        }

        [HttpGet("{id}")]
        [Authorize]
        public IActionResult GetProfileForUser(int id)
        {
            var profileDTO = _userService.GetProfile(id);
            var userId = GetCurrentUserId();

            if (userId == profileDTO.userId)
            {
                profileDTO.own = true;  // Mark profile as owned by the current user
            }

            return Ok(profileDTO);
        }

        [HttpPost("updateAvatar")]
        [Authorize]
        public IActionResult UpdateAvatar([FromBody] UpdateAvatarDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.UpdateAvatar(dto, userId);
                return Ok(new { Message = "Avatar updated successfully" });
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = ex.Message });
            }
        }

        private int GetCurrentUserId()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (userId == null)
            {
                throw new Exception("User ID not found");
            }
            return int.Parse(userId);
        }
    }
}
