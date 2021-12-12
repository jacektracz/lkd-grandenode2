using Grand.Business.Checkout.Interfaces.Payments;
using Grand.Infrastructure;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Payments.Przelewy24V1.Services;

namespace Payments.Przelewy24V1
{
    public class StartupApplication : IStartupApplication
    {
        public void ConfigureServices(IServiceCollection services, IConfiguration configuration)
        {
            services.AddScoped<IPaymentProvider, Przelewy24V1PaymentProvider>();
            services.AddHttpClient<IPrzelewy24V1HttpClient, Przelewy24V1HttpClient>();
        }

        public int Priority => 10;
        public void Configure(IApplicationBuilder application, IWebHostEnvironment webHostEnvironment)
        {

        }
        public bool BeforeConfigure => false;
    }
}
