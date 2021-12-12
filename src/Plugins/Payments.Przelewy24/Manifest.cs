using Grand.Infrastructure;
using Grand.Infrastructure.Plugins;
using Payments.Przelewy24V1;

[assembly: PluginInfo(
    FriendlyName = "Blue Media Standard",
    Group = "Payment methods",
    SystemName = Przelewy24V1PaymentDefaults.ProviderSystemName,
    SupportedVersion = GrandVersion.SupportedPluginVersion,
    Author = "lakida team",
    Version = "1.00"
)]