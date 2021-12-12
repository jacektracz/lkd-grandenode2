using Grand.Domain.Payments;
using Microsoft.AspNetCore.Http;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace Payments.Przelewy24V1
{
    /// <summary>
    /// Represents paypal helper
    /// </summary>
    public static class Przelewy24V1Helper
    {
        public static string OrderTotalSentToPrzelewy24V1 => "OrderTotalSentToPrzelewy24V1";

        public static string Przelewy24V1UrlSandbox => "https://www.sandbox.paypal.com/us/cgi-bin/webscr";
        public static string Przelewy24V1Url => "https://www.paypal.com/us/cgi-bin/webscr";

        /// <summary>
        /// Gets a payment status
        /// </summary>
        /// <param name="paymentStatus">Przelewy24V1 payment status</param>
        /// <param name="pendingReason">Przelewy24V1 pending reason</param>
        /// <returns>Payment status</returns>
        public static PaymentStatus GetPaymentStatus(string paymentStatus, string pendingReason)
        {
            var result = PaymentStatus.Pending;

            if (paymentStatus == null)
                paymentStatus = string.Empty;

            if (pendingReason == null)
                pendingReason = string.Empty;

            switch (paymentStatus.ToLowerInvariant())
            {
                case "pending":
                    switch (pendingReason.ToLowerInvariant())
                    {
                        case "authorization":
                            result = PaymentStatus.Authorized;
                            break;
                        default:
                            result = PaymentStatus.Pending;
                            break;
                    }
                    break;
                case "processed":
                case "completed":
                case "canceled_reversal":
                    result = PaymentStatus.Paid;
                    break;
                case "denied":
                case "expired":
                case "failed":
                case "voided":
                    result = PaymentStatus.Voided;
                    break;
                case "refunded":
                case "reversed":
                    result = PaymentStatus.Refunded;
                    break;
                default:
                    break;
            }
            return result;
        }
    }
}

