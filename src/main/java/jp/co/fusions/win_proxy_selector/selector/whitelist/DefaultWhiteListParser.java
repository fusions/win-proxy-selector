package jp.co.fusions.win_proxy_selector.selector.whitelist;

import java.util.ArrayList;
import java.util.List;

import jp.co.fusions.win_proxy_selector.util.UriFilter;

/*****************************************************************************
 * Default implementation for an white list parser. This will support the most
 * common forms of filters found in white lists. The white list is a comma (or
 * space) separated list of domain names or IP addresses. The following section
 * shows some examples.
 * 
 * .mynet.com - Filters all host names ending with .mynet.com *.mynet.com -
 * Filters all host names ending with .mynet.com www.mynet.* - Filters all host
 * names starting with www.mynet. 123.12.32.1 - Filters the IP 123.12.32.1
 * 123.12.32.1/255 - Filters the IP range http://www.mynet.com - Filters only
 * HTTP protocol not FTP and no HTTPS
 * 
 * Example of a list:
 * 
 * .mynet.com, *.my-other-net.org, 123.55.23.222, 123.55.23.0/24
 * 
 * Some info about this topic can be found here:
 * http://kb.mozillazine.org/No_proxy_for
 * http://technet.microsoft.com/en-us/library/dd361953.aspx
 * 
 * Note that this implementation does not cover all variations of all browsers
 * but should cover the most used formats.
 * 
 * @author Markus Bernhardt, Copyright 2016
 * @author Bernd Rosstauscher, Copyright 2009
 ****************************************************************************/

public class DefaultWhiteListParser implements WhiteListParser {

	/*************************************************************************
	 * parseWhiteList
	 * 
	 * @see WhiteListParser#parseWhiteList(java.lang.String)
	 ************************************************************************/

	public List<UriFilter> parseWhiteList(String whiteList) {
		List<UriFilter> result = new ArrayList<UriFilter>();

		// Allow commas, semicolons, and spaces as separators, with preceding or succeeding spaces.
		String[] token = whiteList.split("((\\s)*[,;\\s](\\s)*)+");
		for (int i = 0; i < token.length; i++) {
			String tkn = token[i].trim();
			if (isIP4SubnetFilter(tkn) || isIP6SubnetFilter(tkn)) {
				result.add(new IpRangeFilter(tkn));
			} else if (tkn.trim().equals("<local>")) {
				result.add(new LocalByPassFilter());
			} else {
				result.add(new HostnameFilter(tkn));
			}
		}

		return result;
	}

	/*************************************************************************
	 * Checks if the given token is an IP6 subnet filter.
	 * 
	 * @param token
	 *            to analyze.
	 * @return true if it is a valid IP6 subnet filter else false.
	 ************************************************************************/

	private boolean isIP6SubnetFilter(String token) {
		return IPWithSubnetChecker.isValidIP6Range(token);
	}

	/*************************************************************************
	 * Checks if the given token is an IP4 subnet filter.
	 * 
	 * @param token
	 *            to analyze.
	 * @return true if it is a valid IP4 subnet filter else false.
	 ************************************************************************/

	private boolean isIP4SubnetFilter(String token) {
		return IPWithSubnetChecker.isValidIP4Range(token);
	}

}
