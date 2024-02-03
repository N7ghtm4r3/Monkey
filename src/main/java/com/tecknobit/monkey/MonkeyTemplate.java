package com.tecknobit.monkey;

import com.tecknobit.apimanager.formatters.JsonHelper;
import org.json.JSONObject;

import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.*;

public class MonkeyTemplate {

    public enum MonkeyTemplateTag {

        PRIMARY_COLOR_TAG("primary_color"),

        SECONDARY_COLOR_TAG("secondary_color"),

        TERTIARY_COLOR_TAG("tertiary_color"),

        TEXT_COLOR_TAG("text_color"),

        LOGO_LINK_TAG("logo_link"),

        LOGO_URL_TAG("logo_url"),

        TITLE_TAG("title_text"),

        DESCRIPTION_TAG("description"),

        FOOTER_TEXT_TAG("footer_text"),

        REASONS_TEXT_TAG("reasons"),

        VERIFICATION_CODE_TAG("verification_code");

        private final String tag;

        private final String value;

        MonkeyTemplateTag(String tag) {
            value = tag;
            this.tag = "<" + tag + ">";
        }

        public String getValue() {
            return value;
        }

        public String getTag() {
            return tag;
        }

    }

    private final MonkeyColorsScheme colorsScheme;

    private final MonkeyLogo monkeyLogo;

    private final MonkeyTextTemplate monkeyTextTemplate;

    public MonkeyTemplate(MonkeyColorsScheme colorsScheme, MonkeyLogo monkeyLogo, MonkeyTextTemplate monkeyTextTemplate) {
        this.colorsScheme = colorsScheme;
        this.monkeyLogo = monkeyLogo;
        this.monkeyTextTemplate = monkeyTextTemplate;
    }

    public MonkeyColorsScheme getColorsScheme() {
        return colorsScheme;
    }

    public MonkeyLogo getMonkeyLogo() {
        return monkeyLogo;
    }

    public MonkeyTextTemplate getMonkeyTextTemplate() {
        return monkeyTextTemplate;
    }

    public static class MonkeyLogo {

        private final String logoLink;

        private final String logoUrl;

        public MonkeyLogo(String logoLink, String logoUrl) {
            this.logoLink = logoLink;
            this.logoUrl = logoUrl;
        }

        public String getLogoLink() {
            return logoLink;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

    }

    public static class MonkeyColorsScheme {

        private final String primaryColor;

        private final String secondaryColor;

        private final String tertiaryColor;

        private final String textColor;

        public MonkeyColorsScheme(JSONObject colorsScheme) {
            this(colorsScheme.toString());
        }

        public MonkeyColorsScheme(String colorsScheme) {
            JsonHelper hColorsScheme = new JsonHelper(colorsScheme);
            String tmpColor = hColorsScheme.getString(PRIMARY_COLOR_TAG.value);
            if(tmpColor == null)
                throw new IllegalArgumentException("primary_color must be not null");
            this.primaryColor = formatColor(tmpColor);
            tmpColor = hColorsScheme.getString(SECONDARY_COLOR_TAG.value);
            if(tmpColor == null)
                throw new IllegalArgumentException("secondary_color must be not null");
            this.secondaryColor = formatColor(tmpColor);
            this.tertiaryColor = formatColor(hColorsScheme.getString(TERTIARY_COLOR_TAG.value));
            this.textColor = formatColor(hColorsScheme.getString(TEXT_COLOR_TAG.value));
        }

        public MonkeyColorsScheme(String primaryColor, String secondaryColor) {
            this(primaryColor, secondaryColor, null, null);
        }

        public MonkeyColorsScheme(String primaryColor, String secondaryColor, String tertiaryColor) {
            this(primaryColor, secondaryColor, tertiaryColor, null);
        }

        public MonkeyColorsScheme(String primaryColor, String secondaryColor, String tertiaryColor, String textColor) {
            if(primaryColor == null)
                throw new IllegalArgumentException("primary_color must be not null");
            this.primaryColor = formatColor(primaryColor);
            if(secondaryColor == null)
                throw new IllegalArgumentException("secondary_color must be not null");
            this.secondaryColor = formatColor(secondaryColor);
            this.tertiaryColor = formatColor(tertiaryColor);
            this.textColor = formatColor(textColor);
        }

        private String formatColor(String color) {
            if(color == null)
                return "";
            if(!color.startsWith("#"))
                color = "#" + color;
            return color;
        }

        public String getPrimaryColor() {
            return primaryColor;
        }

        public String getSecondaryColor() {
            return secondaryColor;
        }

        public String getTertiaryColor() {
            return tertiaryColor;
        }

        public String getTextColor() {
            return textColor;
        }

    }

    public static class MonkeyTextTemplate {

        private final String title;

        private final String description;

        private final String footerText;

        private final String reasonsText;

        public MonkeyTextTemplate(String title, String description, String footerText, String reasonsText) {
            this.title = title.replaceAll("\\n", "<br>");
            this.description = description.replaceAll("\\n", "<br>");
            this.footerText = footerText.replaceAll("\\n", "<br>");
            this.reasonsText = reasonsText.replaceAll("\\n", "<br>");
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getFooterText() {
            return footerText;
        }

        public String getReasonsText() {
            return reasonsText;
        }

    }

}
