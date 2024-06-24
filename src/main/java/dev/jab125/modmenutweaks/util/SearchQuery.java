package dev.jab125.modmenutweaks.util;

import com.google.common.collect.Lists;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface SearchQuery {
	public static final char QUERY_PREFIX = '@';
	default boolean passes(Mod mod, ModsScreen screen) {
		return passes(mod);
	}
	boolean passes(Mod mod);
	String[] filterNames(); // Translated name
	String name();

	public static List<ContextAwareSearchQueryWrapper> find(String string) {
		List<ContextAwareSearchQueryWrapper> queries = new ArrayList<>();
		string = string.toLowerCase(Locale.ROOT);
		int i = 0;
		// Example string: "minecraft @client help @end"
		for (String s : split(string)) {
			if (s.startsWith(Character.toString(QUERY_PREFIX)) && s.length() > 1) {
				SearchQuery q;
				if ((q = SearchQueries.from(s.substring(1))) != null) {
					queries.add(new ContextAwareSearchQueryWrapper(q, i, i + s.length()));
				}
			}
			i += s.length();
		}
		return queries;
	}

	private static Iterable<String> split(String string) {
		List<String> s = new ArrayList<>();
		StringBuilder buf = new StringBuilder();
		for (char c : string.toCharArray()) {
			if (c == ' ') {
				s.add(buf.toString());
				buf.setLength(0);
				s.add(" ");
			} else {
				buf.append(c);
			}
		}
		s.add(buf.toString());
		return s;
	}

	public static OrderedText highlight(List<ContextAwareSearchQueryWrapper> context, String original, int firstCharacterIndex) {
		List<OrderedText> list = Lists.<OrderedText>newArrayList();
		int i = 0;
		//int j = -1;
		//ContextAwareSearchQueryWrapper last = context.getLast();
		//CommandContextBuilder<CommandSource> commandContextBuilder = parse.getContext().getLastChild();

		for (ContextAwareSearchQueryWrapper parsedArgument : context) {
//			if (++j >= HIGHLIGHT_STYLES.size()) {
//				j = 0;
//			}

			int k = Math.max(parsedArgument.start() - firstCharacterIndex, 0);
			if (k >= original.length()) {
				break;
			}

			int l = Math.min(parsedArgument.end() - firstCharacterIndex, original.length());
			if (l > 0) {
				list.add(OrderedText.styledForwardsVisitedString(original.substring(i, k), Style.EMPTY));
				list.add(OrderedText.styledForwardsVisitedString(original.substring(k, l), (Style)Style.EMPTY.withColor(parsedArgument.query().textColor())));
				i = l;
			}
		}

		if (/*parse.getReader().canRead()*/false) {
//			int m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0);
//			if (m < original.length()) {
//				int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
//				list.add(OrderedText.styledForwardsVisitedString(original.substring(i, m), INFO_STYLE));
//				list.add(OrderedText.styledForwardsVisitedString(original.substring(m, n), ERROR_STYLE));
//				i = n;
//			}
		}

		list.add(OrderedText.styledForwardsVisitedString(original.substring(i), Style.EMPTY));
		return OrderedText.concat(list);
	}

	TextColor textColor();
}
