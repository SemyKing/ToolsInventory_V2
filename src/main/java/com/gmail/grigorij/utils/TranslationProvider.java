package com.gmail.grigorij.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class TranslationProvider implements I18NProvider {

	public static final String BUNDLE_PREFIX = "translate";

	public final Locale LOCALE_FI = new Locale("fi", "FI");
	public final Locale LOCALE_EN = new Locale("en", "GB");

	private List<Locale> locales = Collections.unmodifiableList(Arrays.asList(LOCALE_FI, LOCALE_EN));

	private static final LoadingCache<Locale, ResourceBundle> bundleCache = CacheBuilder
			.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
			.build(new CacheLoader<Locale, ResourceBundle>() {

				@Override
				public ResourceBundle load(final Locale key) throws Exception {
					return initializeBundle(key);
				}
			});

	@Override
	public List<Locale> getProvidedLocales() {
		return locales;
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		if (key == null) {
			LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Got lang request for key with null value!");
			return "";
		}

		final ResourceBundle bundle = bundleCache.getUnchecked(locale);

		String value;
		try {
			value = bundle.getString(key);
		} catch (final MissingResourceException e) {
			LoggerFactory.getLogger(TranslationProvider.class.getName())
					.warn("Missing resource", e);
			return "!" + locale.getLanguage() + ": " + key;
		}
		if (params.length > 0) {
			value = MessageFormat.format(value, params);
		}
		return value;
	}

	private static ResourceBundle initializeBundle(final Locale locale) {
		return readProperties(locale);
	}

	protected static ResourceBundle readProperties(final Locale locale) {
		final ClassLoader cl = TranslationProvider.class.getClassLoader();

		ResourceBundle propertiesBundle = null;
		try {
			propertiesBundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale,
					cl);
		} catch (final MissingResourceException e) {
			LoggerFactory.getLogger(TranslationProvider.class.getName())
					.warn("Missing resource", e);
		}
		return propertiesBundle;
	}
}
