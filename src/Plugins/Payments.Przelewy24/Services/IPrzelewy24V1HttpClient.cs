using System.Collections.Generic;
using System.Threading.Tasks;

namespace Payments.Przelewy24V1.Services
{
    public interface IPrzelewy24V1HttpClient
    {
        Task<(bool success, Dictionary<string, string> values)> VerifyIpn(string formString);
        Task<(bool status, Dictionary<string, string> values, string response)> GetPdtDetails(string tx);
    }
}
