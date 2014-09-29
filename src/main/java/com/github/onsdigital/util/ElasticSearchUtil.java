package com.github.onsdigital.util;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;

import com.github.onsdigital.bean.SearchResult;
import com.github.onsdigital.common.ClosedConnectionException;

/**
 * 
 * <p>
 * {@link ElasticSearchUtil} connects to an elastic search cluster and perform
 * search operation under indexes or types.
 * </p>
 * 
 * <p>
 * This class does not create an embedded non-data node. It simply creates a
 * transport client that sniffs available nodes through connected node. It
 * is lightweight and still highly available
 * </p>
 * 
 * <p>
 * See more at
 * http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api
 * /current/client.html <a> </a>
 * </p>
 * 
 * @author Bren
 *
 */

public class ElasticSearchUtil {

	private SearchConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            A {@link SearchConnectionManager} is required to perform
	 *            search operations. Connection must be open to perform any
	 *            search operation
	 */
	public ElasticSearchUtil(SearchConnectionManager connectionManager) {
		this.connectionManager = connectionManager;

	}

	/**
	 * 
	 * Performs the search and returns documents as a list of maps that contains
	 * key-value pairs
	 * 
	 * @param queryBuilder
	 * @return {@link SearchResult}
	 * @throws ClosedConnectionException
	 */
	public SearchResult search(ONSQueryBuilder queryBuilder) {
		testConnection();
		SearchResponse searchResponse = execute(queryBuilder);
		return new SearchResult(searchResponse);
	}

	private SearchResponse execute(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder requestBuilder = buildRequest(queryBuilder);
		return requestBuilder.execute().actionGet();
	}

	private SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder requestBuilder = connectionManager.getClient()
				.prepareSearch(queryBuilder.getIndex())
				.setQuery(queryBuilder.buildQuery());

		for (String field : queryBuilder.getFields()) {
			requestBuilder.addHighlightedField(field);
		}

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			requestBuilder.setTypes(type);
		}
		return requestBuilder;
	}

	/**
	 * 
	 */
	private void testConnection() {
		if (connectionManager.isConnected() == false) {
			throw new ClosedConnectionException("Connection is closed");
		}
	}

	public SearchConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(SearchConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
