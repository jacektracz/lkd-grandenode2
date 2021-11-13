using System.Collections.Generic;
using System.Threading.Tasks;

namespace Payments.BlueMediaV1.Services
{
    public interface IBlueMediaV1HttpClient
    {
        Task<(bool success, Dictionary<string, string> values)> VerifyIpn(string formString);
        Task<(bool status, Dictionary<string, string> values, string response)> GetPdtDetails(string tx);
    }
}
