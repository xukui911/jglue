package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.utils.JVMUtils;

public class SshExecFunc extends AbstractFunc {

	public SshExecFunc() {
		super("sshExec");
		addParam("command");
	}

	@Override
	public Object exec(JGlueContext context, Object[] commands) {
		if(JVMUtils.isWindowsOs()) {
			//throw new GlueException("is not support OS windows!");
		}
		for(Object command: commands) {
			if(command!=null) {
				String[] comds = command.toString().split(";");
				for(String comd: comds) {
					System.out.println("exec command:" + comd);
					if (JVMUtils.isWindowsOs()) {
						comd = "cmd.exe /c " + comd;
					}
					String result = JVMUtils.execCommand(comd);
					System.out.println(result);
					if (result.endsWith("error")) {
						return null;
					}
					
				}
			}
		}
		return null;
	}

	
}
