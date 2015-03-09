package net.jful.dynamiclistview.interfaces;

import net.jful.dynamiclistview.DynamicListLayout;
import net.jful.dynamiclistview.DynamicListLayout.PullingMode;
import net.jful.dynamiclistview.DynamicListLayout.PullingStatus;
import net.jful.dynamiclistview.DynamicListLayout.ScrollDirection;

public interface Listener {
	
	/**
	 * This callback is called when pulling-state changed.
	 * @param layout
	 * @param layoutChild
	 * @param status
	 * @param pulling
	 */
	void onPullingStatusChanged(DynamicListLayout layout, DynamicListLayoutChild layoutChild, PullingStatus status,
			PullingMode pulling);

	/**
	 * This callback is invoked when the list is released.
	 * @param layout
	 * @param layoutChild
	 * @param status
	 * @param pulling
	 */
	void onRelease(DynamicListLayout layout, DynamicListLayoutChild layoutChild, PullingMode mode, PullingStatus status);

	/**
	 * This callback is invoked when the list is closed.
	 * @param layout
	 * @param layoutChild
	 * @param status
	 * @param pulling
	 */
	void onCloesed(DynamicListLayout layout, DynamicListLayoutChild layoutChild, PullingMode mode, boolean completelyClosed);

	/**
	 * This callback is called when scroll-direction changed.
	 * @param layout
	 * @param layoutChild
	 * @param status
	 * @param pulling
	 */
	void onScrollDirectionChanged(DynamicListLayout layout, DynamicListLayoutChild layoutChild, ScrollDirection status);
}