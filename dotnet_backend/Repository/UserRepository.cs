using dotnet_backend.Model;
using WebNet.Data;

namespace dotnet_backend.Repository
{
    public class UserRepository : IUserRepository
    {
        private readonly ApplicationDBContext _context;

        public UserRepository(ApplicationDBContext context)
        {
            _context = context;
        }
        public User? findById(int id)
        {
            return _context.Users.SingleOrDefault(u => u.Id == id);
        }
    }
}
