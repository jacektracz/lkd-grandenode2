﻿using Grand.Business.Common.Interfaces.Logging;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Payments.BlueMediaV1.Services
{
    public class BlueMediaV1HttpClient : IBlueMediaV1HttpClient
    {
        private readonly HttpClient _client;
        private readonly BlueMediaV1PaymentSettings _paypalStandardPaymentSettings;
        private readonly ILogger _logger;
        public BlueMediaV1HttpClient(HttpClient client, BlueMediaV1PaymentSettings paypalStandardPaymentSettings, ILogger logger)
        {
            _client = client;
            _paypalStandardPaymentSettings = paypalStandardPaymentSettings;
            _logger = logger;
        }

        // <summary>
        /// Gets IPN BlueMediaV1 URL
        /// </summary>
        /// <returns></returns>
        private string GetIpnPaypalUrl()
        {
            return _paypalStandardPaymentSettings.UseSandbox ?
                "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr" :
                "https://ipnpb.paypal.com/cgi-bin/webscr";
        }

        public virtual async Task<(bool success, Dictionary<string, string> values)> VerifyIpn(string formString)
        {
            var formContent = new StringContent($"cmd=_notify-validate&{formString}",
                Encoding.UTF8, "application/x-www-form-urlencoded");
            var response = await _client.PostAsync(GetIpnPaypalUrl(), formContent);
            var content = string.Empty;
            try
            {
                response.EnsureSuccessStatusCode();
                content = await response.Content.ReadAsStringAsync();
            }
            catch (Exception ex)
            {
                await _logger.InsertLog(Grand.Domain.Logging.LogLevel.Error, "VerifyIpn", ex.Message);
            }


            var success = content.Trim().Equals("VERIFIED", StringComparison.OrdinalIgnoreCase);

            var values = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);
            foreach (var l in formString.Split('&'))
            {
                var line = l.Trim();
                var equalPox = line.IndexOf('=');
                if (equalPox >= 0)
                    values.Add(line.Substring(0, equalPox), line.Substring(equalPox + 1));
            }
            return (success, values);
        }

        /// <summary>
        /// Gets BlueMediaV1 PDT URL
        /// </summary>
        /// <returns></returns>
        private string GetPaypalPdtUrl()
        {
            return _paypalStandardPaymentSettings.UseSandbox ?
                BlueMediaV1Helper.BlueMediaV1UrlSandbox :
                BlueMediaV1Helper.BlueMediaV1Url;
        }

        public virtual async Task<(bool status, Dictionary<string, string> values, string response)> GetPdtDetails(string tx)
        {
            var formContent = new StringContent($"cmd=_notify-synch&at={_paypalStandardPaymentSettings.PdtToken}&tx={tx}",
               Encoding.UTF8, "application/x-www-form-urlencoded");

            var response = await _client.PostAsync(GetPaypalPdtUrl(), formContent);
            try
            {
                response.EnsureSuccessStatusCode();
            }
            catch(Exception ex)
            {
                await _logger.InsertLog(Grand.Domain.Logging.LogLevel.Error, "GetPdtDetails", ex.Message);
            }
            var content = await response.Content.ReadAsStringAsync();

            var values = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);
            bool firstLine = true, success = false;
            foreach (var l in content.Split('\n'))
            {
                var line = l.Trim();
                if (firstLine)
                {
                    success = line.Equals("SUCCESS", StringComparison.OrdinalIgnoreCase);
                    firstLine = false;
                }
                else
                {
                    var equalPox = line.IndexOf('=');
                    if (equalPox >= 0)
                        values.Add(line.Substring(0, equalPox), line.Substring(equalPox + 1));
                }
            }

            return (success, values, content);
        }
    }
}
