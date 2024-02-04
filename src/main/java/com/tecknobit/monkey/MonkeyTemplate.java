package com.tecknobit.monkey;

import com.tecknobit.apimanager.formatters.JsonHelper;
import org.json.JSONObject;

import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.*;

/**
 * The {@code MonkeyTemplate} class is useful to create a template for the verification email to send
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class MonkeyTemplate {

    /**
     * {@code MonkeyTemplateTag} list of available tags to use in the template
     */
    public enum MonkeyTemplateTag {

        /**
         * {@code PRIMARY_COLOR_TAG} tag for the primary color item
         */
        PRIMARY_COLOR_TAG("primary_color"),

        /**
         * {@code SECONDARY_COLOR_TAG} tag for the secondary color item
         */
        SECONDARY_COLOR_TAG("secondary_color"),

        /**
         * {@code TERTIARY_COLOR_TAG} tag for the tertiary color item
         */
        TERTIARY_COLOR_TAG("tertiary_color"),

        /**
         * {@code TEXT_COLOR_TAG} tag for the texts of the email color item
         */
        TEXT_COLOR_TAG("text_color"),

        /**
         * {@code LOGO_LINK_TAG} tag for the link to open when the logo is clicked
         */
        LOGO_LINK_TAG("logo_link"),

        /**
         * {@code LOGO_URL_TAG} tag for the logo to insert
         */
        LOGO_URL_TAG("logo_url"),

        /**
         * {@code TITLE_TAG} tag for the title of the email to insert
         */
        TITLE_TAG("title_text"),

        /**
         * {@code DESCRIPTION_TAG} tag for the description of the email to insert
         */
        DESCRIPTION_TAG("description"),

        /**
         * {@code FOOTER_TEXT_TAG} tag for the text of the email footer to insert
         */
        FOOTER_TEXT_TAG("footer_text"),

        /**
         * {@code REASONS_TEXT_TAG} tag for the reasons text why the email was sent
         */
        REASONS_TEXT_TAG("reasons"),

        /**
         * {@code VERIFICATION_CODE_TAG} tag to replace with the verification code generated
         */
        VERIFICATION_CODE_TAG("verification_code");

        /**
         * {@code tag} the tag value
         */
        private final String tag;

        /**
         * {@code value} the value of the tag
         */
        private final String value;

        /**
         * Constructor to init the {@link MonkeyTemplateTag} class
         *
         * @param tag: the tag value
         *
         */
        MonkeyTemplateTag(String tag) {
            value = tag;
            this.tag = "<" + tag + ">";
        }

        /**
         * Method to get {@link #value} instance <br>
         * No-any params required
         *
         * @return {@link #value} instance as {@link String}
         */
        public String getValue() {
            return value;
        }

        /**
         * Method to get {@link #tag} instance <br>
         * No-any params required
         *
         * @return {@link #tag} instance as {@link String}
         */
        public String getTag() {
            return tag;
        }

    }

    /**
     * {@code colorsScheme} the colors scheme of the template
     */
    private final MonkeyColorsScheme colorsScheme;

    /**
     * {@code monkeyLogo} the logo details to insert in the email
     */
    private final MonkeyLogo monkeyLogo;

    /**
     * {@code monkeyTextTemplate} the texts to insert in the email
     */
    private final MonkeyTextTemplate monkeyTextTemplate;

    /**
     * Constructor to init the {@link MonkeyTemplate} class
     *
     * @param colorsScheme: the colors scheme of the template
     * @param monkeyLogo: the logo details to insert in the email
     * @param monkeyTextTemplate: the texts to insert in the email
     *
     */
    public MonkeyTemplate(MonkeyColorsScheme colorsScheme, MonkeyLogo monkeyLogo, MonkeyTextTemplate monkeyTextTemplate) {
        this.colorsScheme = colorsScheme;
        this.monkeyLogo = monkeyLogo;
        this.monkeyTextTemplate = monkeyTextTemplate;
    }

    /**
     * Method to get {@link #colorsScheme} instance <br>
     * No-any params required
     *
     * @return {@link #colorsScheme} instance as {@link MonkeyColorsScheme}
     */
    public MonkeyColorsScheme getColorsScheme() {
        return colorsScheme;
    }

    /**
     * Method to get {@link #monkeyLogo} instance <br>
     * No-any params required
     *
     * @return {@link #monkeyLogo} instance as {@link MonkeyLogo}
     */
    public MonkeyLogo getMonkeyLogo() {
        return monkeyLogo;
    }

    /**
     * Method to get {@link #monkeyTextTemplate} instance <br>
     * No-any params required
     *
     * @return {@link #monkeyTextTemplate} instance as {@link MonkeyTextTemplate}
     */
    public MonkeyTextTemplate getMonkeyTextTemplate() {
        return monkeyTextTemplate;
    }

    /**
     * The {@code MonkeyLogo} class is useful to insert the details of the logo to insert in the email
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public static class MonkeyLogo {

        /**
         * {@code logoLink} link to open when the logo is clicked
         */
        private final String logoLink;

        /**
         * {@code logoUrl} url of the logo to insert
         */
        private final String logoUrl;

        /**
         * Constructor to init the {@link MonkeyLogo} class
         *
         * @param logoLink: link to open when the logo is clicked
         * @param logoUrl: url of the logo to insert
         *
         */
        public MonkeyLogo(String logoLink, String logoUrl) {
            this.logoLink = logoLink;
            this.logoUrl = logoUrl;
        }

        /**
         * Method to get {@link #logoLink} instance <br>
         * No-any params required
         *
         * @return {@link #logoLink} instance as {@link String}
         */
        public String getLogoLink() {
            return logoLink;
        }

        /**
         * Method to get {@link #logoUrl} instance <br>
         * No-any params required
         *
         * @return {@link #logoUrl} instance as {@link String}
         */
        public String getLogoUrl() {
            return logoUrl;
        }

    }

    /**
     * The {@code MonkeyColorsScheme} class is useful to create the colors scheme for the email
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public static class MonkeyColorsScheme {

        /**
         * {@code primaryColor} the primary color value
         */
        private final String primaryColor;

        /**
         * {@code secondaryColor} the secondary color value
         */
        private final String secondaryColor;

        /**
         * {@code tertiaryColor} the tertiary color value
         */
        private final String tertiaryColor;

        /**
         * {@code textsColor} the texts color value
         */
        private final String textsColor;

        /**
         * Constructor to init the {@link MonkeyColorsScheme} class
         *
         * @param colorsScheme: the colors scheme to use
         * <pre>
         * {@code
         *      {
         *         "primary_color": "#required",
         *         "secondary_color": "#required",
         *         "tertiary_color": "#default_if_not_filled",
         *         "texts_color": "#default_if_not_filled"
         *      }
         * }
         * </pre>
         *
         */
        public MonkeyColorsScheme(JSONObject colorsScheme) {
            this(colorsScheme.toString());
        }

        /**
         * Constructor to init the {@link MonkeyColorsScheme} class
         *
         * @param colorsScheme: the colors scheme to use
         * <pre>
         * {@code
         *      {
         *         "primary_color": "#required",
         *         "secondary_color": "#required",
         *         "tertiary_color": "#default_if_not_filled",
         *         "texts_color": "#default_if_not_filled"
         *      }
         * }
         * </pre>
         * @throws IllegalArgumentException when a required color is missing in the scheme
         */
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
            this.textsColor = formatColor(hColorsScheme.getString(TEXT_COLOR_TAG.value));
        }

        /**
         * Constructor to init the {@link MonkeyColorsScheme} class
         *
         * @param primaryColor: the primary color value
         * @param secondaryColor: the secondary color value
         *
         */
        public MonkeyColorsScheme(String primaryColor, String secondaryColor) {
            this(primaryColor, secondaryColor, null, null);
        }

        /**
         * Constructor to init the {@link MonkeyColorsScheme} class
         *
         * @param primaryColor: the primary color value
         * @param secondaryColor: the secondary color value
         * @param tertiaryColor: the tertiary color value
         *
         */
        public MonkeyColorsScheme(String primaryColor, String secondaryColor, String tertiaryColor) {
            this(primaryColor, secondaryColor, tertiaryColor, null);
        }

        /**
         * Constructor to init the {@link MonkeyColorsScheme} class
         *
         * @param primaryColor: the primary color value
         * @param secondaryColor: the secondary color value
         * @param tertiaryColor: the tertiary color value
         * @param textsColor: the texts color value
         *
         * @throws IllegalArgumentException when a required color is missing in the scheme
         */
        public MonkeyColorsScheme(String primaryColor, String secondaryColor, String tertiaryColor, String textsColor) {
            if(primaryColor == null)
                throw new IllegalArgumentException("primary_color must be not null");
            this.primaryColor = formatColor(primaryColor);
            if(secondaryColor == null)
                throw new IllegalArgumentException("secondary_color must be not null");
            this.secondaryColor = formatColor(secondaryColor);
            this.tertiaryColor = formatColor(tertiaryColor);
            this.textsColor = formatColor(textsColor);
        }

        /**
         * Method to format the color correctly
         *
         * @param color: the color to format
         * @return color formatted as {@link String}
         */
        private String formatColor(String color) {
            if(color == null)
                return "";
            if(!color.startsWith("#"))
                color = "#" + color;
            return color;
        }

        /**
         * Method to get {@link #primaryColor} instance <br>
         * No-any params required
         *
         * @return {@link #primaryColor} instance as {@link String}
         */
        public String getPrimaryColor() {
            return primaryColor;
        }

        /**
         * Method to get {@link #secondaryColor} instance <br>
         * No-any params required
         *
         * @return {@link #secondaryColor} instance as {@link String}
         */
        public String getSecondaryColor() {
            return secondaryColor;
        }

        /**
         * Method to get {@link #tertiaryColor} instance <br>
         * No-any params required
         *
         * @return {@link #tertiaryColor} instance as {@link String}
         */
        public String getTertiaryColor() {
            return tertiaryColor;
        }

        /**
         * Method to get {@link #textsColor} instance <br>
         * No-any params required
         *
         * @return {@link #textsColor} instance as {@link String}
         */
        public String getTextsColor() {
            return textsColor;
        }

    }

    /**
     * The {@code MonkeyTextTemplate} class is useful to create the texts to use in the verification email
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public static class MonkeyTextTemplate {

        /**
         * {@code title} the title of the email to insert
         */
        private final String title;

        /**
         * {@code description} the description of the email to insert
         */
        private final String description;

        /**
         * {@code footerText} tag for the text of the email footer to insert
         */
        private final String footerText;

        /**
         * {@code reasonsText} tag for the reasons text why the email was sent
         */
        private final String reasonsText;

        /**
         * Constructor to init the {@link MonkeyTextTemplate} class
         *
         * @param title: the title of the email to insert
         * @param description: the description of the email to insert
         * @param footerText: tag for the text of the email footer to insert
         * @param reasonsText: tag for the reasons text why the email was sent
         *
         */
        public MonkeyTextTemplate(String title, String description, String footerText, String reasonsText) {
            this.title = title.replaceAll("\\n", "<br>");
            this.description = description.replaceAll("\\n", "<br>");
            this.footerText = footerText.replaceAll("\\n", "<br>");
            this.reasonsText = reasonsText.replaceAll("\\n", "<br>");
        }

        /**
         * Method to get {@link #title} instance <br>
         * No-any params required
         *
         * @return {@link #title} instance as {@link String}
         */
        public String getTitle() {
            return title;
        }

        /**
         * Method to get {@link #description} instance <br>
         * No-any params required
         *
         * @return {@link #description} instance as {@link String}
         */
        public String getDescription() {
            return description;
        }

        /**
         * Method to get {@link #footerText} instance <br>
         * No-any params required
         *
         * @return {@link #footerText} instance as {@link String}
         */
        public String getFooterText() {
            return footerText;
        }

        /**
         * Method to get {@link #reasonsText} instance <br>
         * No-any params required
         *
         * @return {@link #reasonsText} instance as {@link String}
         */
        public String getReasonsText() {
            return reasonsText;
        }

    }

}
