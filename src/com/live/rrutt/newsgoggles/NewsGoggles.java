package com.live.rrutt.newsgoggles;

import com.live.rrutt.newsgoggles.lib.PrologLibrary;
import com.live.rrutt.tuprolog.util.*;

import alice.tuprolog.*;
import alice.tuprolog.event.*;

import java.io.*;
import java.util.List;

public class NewsGoggles implements Serializable, OutputListener {

	public Prolog engine;

	private static Boolean testing = false;

	public NewsGoggles(String[] args) {
		System.out
				.println("Rick Rutt's News Goggles - Using the tuProlog system "
						+ Prolog.getVersion());

		PrologLibrary.traceEnabled = false;

		for (String arg : args) {
			if ((arg.length() > 1) && (arg.charAt(0) == '-')) {
				if (arg.equalsIgnoreCase("-trace")) {
					PrologLibrary.traceEnabled = true;
					System.out.println("Trace output enabled.");
				} else if (arg.equalsIgnoreCase("-test")) {
					testing = true;
					System.out.println("Test mode enabled.");
				} else {
					System.out.println("Unknown command argument ignored: "
							+ arg);
				}
			} else {
				System.out.println("Unknown command argument ignored: " + arg);
			}
		}
	}

	private void run() {
		engine = new Prolog();
		try {
			engine.loadLibrary("com.live.rrutt.newsgoggles.lib.PrologLibrary");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		engine.addOutputListener(this);

		try {
			TheoryLoader loader = new TheoryLoader();
			String theoryText = loader.load();

			Theory theory = new Theory(theoryText);
			engine.setTheory(theory);

			if (testing) {
				SolveInfo testInfo = engine.solve("test.");
				if (testInfo.isSuccess()) {
					System.out.println("Test run succeeded.");
				} else {
					System.out.println("Test run did not succeed.");
				}
			} else {
				SolveInfo info = engine
						.solve("all_subscriber_feeds(ResultList).");

				if (info.isSuccess()) {
					System.out.println("Success.");
					List bindings = info.getBindingVars();

					if (PrologLibrary.traceEnabled) {
						System.out.println("Bindings:");
						System.out.println(bindings.toString());
					}

					Var resultList = (Var) bindings.get(0);
					showSubscriberFeedsResultList(resultList);
					
					System.out.println("Done.");
				} else {
					System.out.println("Failure.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSubscriberFeedsResultList(Var resultList) {
		System.out.println("\nAll Subscriber Feeds:");
		
		Struct feedList = (Struct) resultList.getTerm();
		while (!feedList.isEmptyList()) {
			Struct subscriberFeed = (Struct) feedList.getArg(0)
					.getTerm();
			feedList = (Struct) feedList.getArg(1).getTerm();

			// System.out.println("  " + subscriberFeed.toString());

			Struct subscriber = (Struct) subscriberFeed.getArg(0)
					.getTerm();
			Struct articleList = (Struct) subscriberFeed.getArg(1)
					.getTerm();

			// System.out.println("  " + subscriber.getName() + " "
			// + articleList.toString());
			System.out.println("\n  Feed for " + subscriber.getName()
					+ ":");

			while (!articleList.isEmptyList()) {
				Struct article = (Struct) articleList.getArg(0)
						.getTerm();
				articleList = (Struct) articleList.getArg(1)
						.getTerm();

				String articleId = PrologLibrary
						.stringValueFromTerm(article.getArg(0));
				String provider = PrologLibrary
						.stringValueFromTerm(article.getArg(1));
				String contents = PrologLibrary
						.stringValueFromTerm(article.getArg(2));

				System.out.println("    #" + articleId.toString()
						+ " from " + provider + ": " + contents);
			}
		}
		
		System.out.println("\n(End of Feeds.)");
	}

	public void onOutput(OutputEvent ev) {
		String s = Utilities.stripQuotes(ev.getMsg());
		System.out.print(s);
	}

	public static void main(String args[]) {
		new NewsGoggles(args).run();
	}
}
