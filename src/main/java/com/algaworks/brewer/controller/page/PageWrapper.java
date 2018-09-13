package com.algaworks.brewer.controller.page;

import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class PageWrapper<T> {
	private Page<T> page;
	private URIBuilder pageBuilder;
	private URIBuilder sortBuilder;

	public PageWrapper(Page<T> page, HttpServletRequest request) {
		this.page = page;

		try {
			String httpUrl = getFullRequestUrl(request);
			this.pageBuilder = new URIBuilder(httpUrl);
			this.sortBuilder = new URIBuilder(httpUrl);
		} catch (URISyntaxException e) {
			throw new RuntimeException("{generic.exception.url.invalida}", e);
		}
	}

	public List<T> getContent() {
		return page.getContent();
	}

	public boolean isEmpty() {
		return page.getContent().isEmpty();
	}

	public int getNumber() {
		return page.getNumber();
	}

	public boolean isFirst() {
		return page.isFirst();
	}

	public boolean isLast() {
		return page.isLast();
	}

	public int getTotalPages() {
		return page.getTotalPages();
	}

	public String url(int pageNumber) {
		return pageBuilder.setParameter("page", String.valueOf(pageNumber)).toString();
	}

	public String orderUrl(String property) {
		String valorSort = String.format("%s,%s", property, inverterDirecao(property));
		return sortBuilder.setParameter("sort", valorSort).toString();
	}

	public String inverterDirecao(String property) {
		String direction = "asc";

		Order order = page.getSort() != null ? page.getSort().getOrderFor(property) : null;
		if (order != null) {
			direction = Direction.ASC.equals(order.getDirection()) ? "desc" : "asc";
		}

		return direction;
	}

	public boolean descending(String property) {
		return inverterDirecao(property).equals("asc");
	}

	public boolean ordered(String property) {

		Order order = page.getSort() != null ? page.getSort().getOrderFor(property) : null;
		if (order == null) {
			return false;
		}

		return page.getSort().getOrderFor(property) != null ? true : false;
	}

	private String getFullRequestUrl(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestURL()
				.append(httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "")
				.toString().replace("excluido", "");
	}
}
