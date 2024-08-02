using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using dotnet_backend.Model;
using Microsoft.EntityFrameworkCore;

namespace WebNet.Data
{
    public class ApplicationDBContext : DbContext
    {
        public ApplicationDBContext(DbContextOptions options) : base(options)
        {

        }
        public DbSet<Comment> Comments { get; set; }
        public DbSet<FriendRequest> FriendRequests { get; set; }
        public DbSet<Like> Likes { get; set; }
        public DbSet<User> Users { get; set; }
        public DbSet<Post> Posts { get; set; }
        public DbSet<Notification> Notifications { get; set; }


        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // post
            modelBuilder.Entity<Post>().HasMany(p => p.Comments).WithOne(c => c.Post).HasForeignKey(p => p.PostId);
            modelBuilder.Entity<Post>().HasMany(p => p.Likes).WithOne(l => l.Post).HasForeignKey(p => p.PostId);
          


            // user
            modelBuilder.Entity<User>().HasMany(u => u.Comments).WithOne(u => u.User).HasForeignKey(p => p.UserId);
            modelBuilder.Entity<User>().HasMany(u => u.Posts).WithOne(u => u.User).HasForeignKey(p => p.UserId);
            modelBuilder.Entity<User>().HasMany(u => u.FriendRequests).WithOne(u => u.FromUser).HasForeignKey(p => p.UserId);
            modelBuilder.Entity<User>().HasMany(u => u.Notifications).WithOne(u => u.Trigger).HasForeignKey(p => p.UserId);

            modelBuilder.Entity<User>()
             .HasMany(u => u.Friends)
             .WithMany()
             .UsingEntity<Dictionary<string, object>>(
                 "UserFriends",
                 j => j
                     .HasOne<User>()
                     .WithMany()
                     .HasForeignKey("UserId")
                     .OnDelete(DeleteBehavior.ClientCascade),
                 j => j
                     .HasOne<User>()
                     .WithMany()
                     .HasForeignKey("FriendId")
                     .OnDelete(DeleteBehavior.ClientCascade)
             );
            modelBuilder.Entity<UserFriend>()
           .HasKey(uf => new { uf.UserId, uf.FriendId });
            base.OnModelCreating(modelBuilder);
            
        }


    }
}