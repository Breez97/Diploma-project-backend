package com.breez.constants;

import java.util.Set;

public class Constants {

	// Common
	public static final String COMMON_SORT_POPULAR = "popular";

	public static final String COMMON_SORT_NEW = "new";

	public static final String COMMON_SORT_PRICE_ASC = "priceasc";

	public static final String COMMON_SORT_PRICE_DESC = "pricedesc";

	public static final String COMMON_SORT_RATING = "rating";

	public static final Set<String> SET_AVAILABLE_SORT = Set.of(COMMON_SORT_POPULAR, COMMON_SORT_NEW, COMMON_SORT_PRICE_ASC,
			COMMON_SORT_PRICE_DESC, COMMON_SORT_RATING);

	public static final String COMMON_PAGE = "1";

	// Wildberries
	public static final String WILDBERRIES = "wildberries";

	public static final String WILDBERRIES_EXTERNAL_LINK = "https://www.wildberries.ru";

	public static final String WILDBERRIES_PRODUCT_INFO_LINK = "https://card.wb.ru/cards/v2/detail?appType=1&curr=rub&dest=-1257786&hide_dtype=13&spp=30&ab_testing=false&lang=ru&nm=";

	public static final String WILDBERRIES_BASE_URL = "https://search.wb.ru/exactmatch/ru/common/v9/search?ab_testing=false&appType=1&curr=rub&";

	public static final String WILDBERRIES_X_CAPTCHA_ID = "Catalog 1|1|1745257722|AA==|9d3c8195ec9f40b0abc3eec89fe4985e|GbYNyALGK4iC6A49gulA7O9i1PNDS5l29DKM810XEMM";

	public static final String WILDBERRIES_X_QUERY_ID = "qid533684664174323952120250412112347";

	// По популярности
	public static final String WILDBERRIES_SORT_POPULAR = "popular";
	// По новинкам
	public static final String WILDBERRIES_SORT_NEW = "newly";
	// По пвозрастанию цены
	public static final String WILDBERRIES_SORT_PRICE_ASC = "priceup";
	// По убыванию цены
	public static final String WILDBERRIES_SORT_PRICE_DESC = "pricedown";
	// По рейтингу
	public static final String WILDBERRIES_SORT_RATING = "rate";

	// Ozon
	public static final String OZON = "ozon";

	public static final String OZON_EXTERNAL_LINK = "https://www.ozon.ru";

	public static final String OZON_BASE_URL = "https://www.ozon.ru/api/entrypoint-api.bx/page/json/v2?url=";

	public static final String OZON_COOKIE = "__Secure-access-token=7.0.6T7xez7QSPSJrFFdagKnMA.1.AXVkEH9cc8sVFbGOKxVYE1OYXoX8eRyjcGnJ8OfLMT6ZPxXFrqbWgkdaPtOLtP0rMg..20250405133921.De_6JX4ENbmqEmL-A1Ifm7X28d9j4wkqRe0pEhnlv7s.1bbaffe292dd1244b; __Secure-refresh-token=7.0.6T7xez7QSPSJrFFdagKnMA.1.AXVkEH9cc8sVFbGOKxVYE1OYXoX8eRyjcGnJ8OfLMT6ZPxXFrqbWgkdaPtOLtP0rMg..20250405133921.sYsE0GGK-0HknTc0q83CLOQETnQ2alb1dZs3ZI6jF3o.1ba207b64284dc0c4; __Secure-ab-group=1; __Secure-user-id=0; abt_data=7.WfkSwWeQQAqwTJONGfsBf2YRlUZd9nGXZqJI0O6Fe6Yw71VQJsSz3bAhAamxd_7cjx8KcJEf0T8A5Jos68zMLsz9PQaKE9oot1jpie6f_il7p2He9RlJIFTZRpWk3STivv86YLVFZJ7_QXZ9KRGoeMSwdoi5GJxzYNqNWd8_OrKwDSXHBRFEquL69uVoS5YBZ4DZWPsBo0SJZv2i3p3ZLIWyKeodYh-vhkndeue3EbVhHk5BcGTgWoxqUbxTDJTkmAru7iPTN-HrbdJaM2_fuYV53H3Bbmt4d28vrCHxfdXzN-byoHqxHylhrIt5jBo-mf0He8I-BbvR0IlOF5XpRzfYNEsylbg8ldIzdAHVbqmdd2dQqEYbdBi_UxS3PUfzk8tvU59x9dY3xf2dOnf1r7dvOAqgxXeC6fkUjQE26cdshcYnJF8P1Ka6uYX2xJDmhh3LQcay_oFHK2qOWpH5Cp4CRvnQ2nAOeJSvww; __Secure-ETC=7b236d23650c2612ec63d976c892053e";

	// Популярное
	public static final String OZON_SORT_POPULAR = "score";
	// Новинки
	public static final String OZON_SORT_NEW = "new";
	// Дешевле
	public static final String OZON_SORT_PRICE_ASC = "price";
	// Дороже
	public static final String OZON_SORT_PRICE_DESC = "price_desc";
	// С высоким рейтингом
	public static final String OZON_SORT_RATING = "rating";

}
