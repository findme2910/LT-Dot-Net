using dotnet_backend.Model;

namespace dotnet_backend.Repository
{
    public interface IUserRepository
    {
        User findById(int id);
    }
}
