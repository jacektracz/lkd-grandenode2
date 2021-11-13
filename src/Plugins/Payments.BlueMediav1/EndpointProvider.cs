using Grand.Infrastructure.Endpoints;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Routing;

namespace Payments.BlueMediaV1
{
    public partial class EndpointProvider : IEndpointProvider
    {
        public void RegisterEndpoint(IEndpointRouteBuilder endpointRouteBuilder)
        {
            //PDT
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.BlueMediaV1.PDTHandler",
                 "Plugins/PaymentBlueMediaV1/PDTHandler",
                 new { controller = "PaymentBlueMediaV1", action = "PDTHandler" }
            );
            //IPN
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.BlueMediaV1.IPNHandler",
                 "Plugins/PaymentBlueMediaV1/IPNHandler",
                 new { controller = "PaymentBlueMediaV1", action = "IPNHandler" }
            );
            //Cancel
            endpointRouteBuilder.MapControllerRoute("Plugin.Payments.BlueMediaV1.CancelOrder",
                 "Plugins/PaymentBlueMediaV1/CancelOrder",
                 new { controller = "PaymentBlueMediaV1", action = "CancelOrder" }
            );
        }
        public int Priority => 0;

    }
}
