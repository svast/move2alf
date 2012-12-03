package eu.xenit.move2alf.web.controller;

import java.util.Comparator;

import eu.xenit.move2alf.core.enums.ERole;

public class ERoleComparator implements Comparator<ERole> {

	@Override
	public int compare(ERole arg0, ERole arg1) {
		if(arg0 == arg1){
			return 0;
		}
		if(arg0 == ERole.SYSTEM_ADMIN || arg1 == ERole.CONSUMER){
			return 1;
		}
		if(arg1 == ERole.CONSUMER || arg0 == ERole.SYSTEM_ADMIN){
			return -1;
		}
		if(arg0 == ERole.JOB_ADMIN){
			return 1;
		}
		return -1;
		
	}

}
