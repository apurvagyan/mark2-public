package com.team1678.frc2021.auto;

import java.util.List;

import com.team1678.frc2021.Constants;
import com.team1678.frc2021.commands.AutoAimCommand;
import com.team1678.frc2021.commands.ReadyGyro;
import com.team1678.frc2021.commands.ShootCommand;
import com.team1678.frc2021.subsystems.Superstructure;
import com.team1678.frc2021.subsystems.Swerve;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;

public class ShotLeftFront extends SequentialCommandGroup{

    public ShotLeftFront(Swerve s_Swerve) {

        final Superstructure mSuperstructure = Superstructure.getInstance();
        
        var thetaController =
            new ProfiledPIDController(
                Constants.AutoConstants.kPThetaController, 0, 0, Constants.AutoConstants.kThetaControllerConstraints);

        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        Trajectory moveFront =
            TrajectoryGenerator.generateTrajectory(
                new Pose2d(2.9, 7.5, Rotation2d.fromDegrees(0.0)),
                List.of(),
                new Pose2d(3.9, 7.5 , Rotation2d.fromDegrees(0.0)),
                Constants.AutoConstants.defaultConfig);

        SwerveControllerCommand moveFrontCommand =
            new SwerveControllerCommand(
                moveFront,
                s_Swerve::getPose,
                Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(0),
                s_Swerve::setModuleStates,
                s_Swerve);
        
        ShootCommand shoot =
            new ShootCommand(mSuperstructure);

        AutoAimCommand aim =
            new AutoAimCommand(mSuperstructure, 200);

        ReadyGyro readyGyro = 
            new ReadyGyro(s_Swerve);

        addCommands(
            new InstantCommand(() -> s_Swerve.resetOdometry(new Pose2d(2.9, 7.5, Rotation2d.fromDegrees(0.0)))),
            aim,
            shoot,
            moveFrontCommand,
            readyGyro
        );
    

    }
    
}
