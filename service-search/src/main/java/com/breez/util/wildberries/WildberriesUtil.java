package com.breez.util.wildberries;

import com.breez.util.CommonUtil;
import org.springframework.stereotype.Component;

import static com.breez.constants.Constants.WILDBERRIES_EXTERNAL_LINK;

@Component
public class WildberriesUtil extends CommonUtil {

	protected String getExternalLinkWildberries(long id) {
		return WILDBERRIES_EXTERNAL_LINK + "/catalog/" + id + "/detail.aspx";
	}

	protected String getImageUrl(long id) {
		long vol = id / 100000;
		long part = id / 1000;
		String basketNum = getBasketNum(vol);
		return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp", basketNum, vol, part, id);
	}

	public String getBasketNum(long vol) {
		// List of current baskets
		// https://static-basket-01.wbbasket.ru/vol2/site/j/spa/index.7f239ace2fcf8a7bb122.js
		// search: "break;default:r=""
		if (vol <= 143) return "01";
		else if (vol <= 287) return "02";
		else if (vol <= 431) return "03";
		else if (vol <= 719) return "04";
		else if (vol <= 1007) return "05";
		else if (vol <= 1061) return "06";
		else if (vol <= 1115) return "07";
		else if (vol <= 1169) return "08";
		else if (vol <= 1313) return "09";
		else if (vol <= 1601) return "10";
		else if (vol <= 1655) return "11";
		else if (vol <= 1919) return "12";
		else if (vol <= 2045) return "13";
		else if (vol <= 2189) return "14";
		else if (vol <= 2405) return "15";
		else if (vol <= 2621) return "16";
		else if (vol <= 2837) return "17";
		else if (vol <= 3053) return "18";
		else if (vol <= 3269) return "19";
		else if (vol <= 3485) return "20";
		else if (vol <= 3701) return "21";
		else if (vol <= 3917) return "22";
		else if (vol <= 4133) return "23";
		else if (vol <= 4349) return "24";
		else if (vol <= 4565) return "25";
		else return "26";
	}

}
