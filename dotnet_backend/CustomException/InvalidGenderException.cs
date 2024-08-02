using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.CustomException
{
    public class InvalidGenderException : Exception
    {
        public InvalidGenderException(string message) : base(message) { }
    }
}