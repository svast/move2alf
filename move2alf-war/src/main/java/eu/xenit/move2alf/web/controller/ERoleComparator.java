package eu.xenit.move2alf.web.controller;

import eu.xenit.move2alf.core.enums.ERole;

import java.util.Comparator;

public class ERoleComparator implements Comparator<ERole> {

	@Override
	public int compare(ERole arg0, ERole arg1) {
		if(arg0 == arg1){
			return 0;
		}
		if(arg0 == ERole.ROLE_SYSTEM_ADMIN || arg1 == ERole.ROLE_CONSUMER){
			return 1;
		}
		if(arg0 == ERole.ROLE_JOB_ADMIN){
			return 1;
		}
		return -1;
		
	}

}
