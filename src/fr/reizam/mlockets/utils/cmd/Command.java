package fr.reizam.mlockets.utils.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Framework - Command <br>
 * The command annotation used to designate methods as commands. All methods
 * should have a single CommandArgs argument
 * 
 * @author minnymin3
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	/**
	 * Gets the required permission of the command
	 * 
	 * @return
	 */
	public String permissionNode() default " ";

	/**
	 * A list of alternate names that the command is executed under. See
	 * name() for details on how names work
	 * 
	 * @return
	 */
	public String[] name() default {};

	
	/**
	 * Whether or not the command is available to players only
	 * 
	 * @return
	 */
	public boolean isConsole() default false;
}
