using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.Model;
using dotnet_backend.Service;
using WebNet.Data;
using System.Linq;
using System.Security.Claims;

namespace dotnet_backend.Controller
{
    [Route("post")]
    [ApiController]
    public class PostController : ControllerBase
    {
        private readonly IPostService _postService;
        private readonly IUserService _userService;

        public PostController(IPostService postService, IUserService userService)
        {
            _postService = postService;
            _userService = userService;
        }
        //Chỉnh sửa lại tham số save một chút
        [HttpPost]
        [Authorize]
        public IActionResult Create([FromBody] AddPostDTO dto)
        {
            try
            { var userId = GetCurrentUserId();
                _postService.Save(dto,userId);
                return Ok(new ResponseDTO("Success"));
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }

        [HttpGet]
        [Authorize]
        public IActionResult Get()
        {
            try
            {
                var userId = GetCurrentUserId();
                var posts = _postService.Get(userId);
                var result = posts.Select(post => PostMapper.EntityToViewPostDTO(post)).ToList();
                foreach (var post in posts)
                {
                    var postDTO = result.First(r => r.postId == post.Id);
                    postDTO.liked = post.Likes.Any(l => l.UserId == userId);
                }
                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }
        //Lấy ra danh sách các bài post của một người dựa trên userID
        [HttpGet("get/{id}")]
        [Authorize]
        public IActionResult GetSpecificPost(int id)
        {
            try
            {
                var posts = _postService.GetSpecific(id);
                var result = posts.Select(post => PostMapper.EntityToViewPostDTO(post)).ToList();
                foreach (var post in posts)
                {
                    var postDTO = result.First(r => r.postId == post.Id);
                    postDTO.liked = post.Likes.Any(l => l.UserId == id);
                }
                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }

        //user cũng có rồi
        [HttpPost("comment")]
        [Authorize]
        public IActionResult Comment([FromBody] CommentDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.Comment(dto, userId);
                return Ok(new ResponseDTO("Success"));
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }
        //reply post
        [HttpPost("reply")]
        [Authorize]
        public IActionResult ReplyComment([FromBody] ReplyCommentDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.ReplyComment(dto, userId);
                return Ok(new ResponseDTO("Success"));
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }
        //Lấy ra danh sách các comment trên postID nào đó
        [HttpGet("comment/{postId}")]
        [Authorize]
        public IActionResult GetComments(int postId)
        {
            try
            {
                var comments = _postService.GetComments(postId)
                    .Select(comment => PostMapper.EntityToCommentViewDTO(comment));
                return Ok(comments);
            }
            catch (Exception e)
            {
                return BadRequest(new ResponseDTO("Failure"));
            }
        }
        //lấy id của người dùng hiện tại đăng nhập
        private int GetCurrentUserId()
        {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null)
        {
        throw new Exception("User ID not found");
        }
        return int.Parse(userId);}
    }
}
