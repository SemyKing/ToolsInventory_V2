package com.gmail.grigorij;

import com.vaadin.flow.server.*;

class VaadinSessionListener {

	public static class VaadinSessionInitListener implements SessionInitListener {

		@Override
		public void sessionInit(SessionInitEvent event) {
			try {
				System.out.println("-----New Session Started-----");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class VaadinSessionDestroyListener implements SessionDestroyListener {

		@Override
		public void sessionDestroy(SessionDestroyEvent event) {

			/*
			 * check if HTTP Session is closing
			 */
			if (event.getSession() != null) {
				System.out.println("-----Session Has Ended-----");
			}
		}
	}
}
