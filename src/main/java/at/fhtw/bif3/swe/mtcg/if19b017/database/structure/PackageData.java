package at.fhtw.bif3.swe.mtcg.if19b017.database.structure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect

@AllArgsConstructor
@Data
@NoArgsConstructor

public class PackageData
{
    @JsonProperty
    private String Id;
    @JsonProperty
    private String Name;
    @JsonProperty
    private double Damage;
}
