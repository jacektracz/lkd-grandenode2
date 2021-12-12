using Grand.Infrastructure.Endpoints;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Routing;

namespace Payments.Przelewy24V1
{
    public partial class EndpointProvider : IEndpointProvider
    {
        public void RegisterEndpoint(IEndpointRouteBuilder endpointRouteBuilder)
        {
            //PDT
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.Przelewy24V1.PDTHandler",
                 "Plugins/PaymentPrzelewy24V1/PDTHandler",
                 new { controller = "PaymentPrzelewy24V1", action = "PDTHandler" }
            );
            //IPN
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.Przelewy24V1.IPNHandler",
                 "Plugins/PaymentPrzelewy24V1/IPNHandler",
                 new { controller = "PaymentPrzelewy24V1", action = "IPNHandler" }
            );
            //Cancel
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.Przelewy24V1.CancelOrder",
                 "Plugins/PaymentPrzelewy24V1/CancelOrder",
                 new { controller = "PaymentPrzelewy24V1", action = "CancelOrder" }
            );
        }
        public int Priority => 0;

    }
}
