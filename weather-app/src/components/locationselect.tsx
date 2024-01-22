"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { Check, ChevronsUpDown } from "lucide-react";
import { useForm } from "react-hook-form";
import * as z from "zod";

import { cn } from "@/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from "@/components/ui/command";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { toast } from "@/components/ui/use-toast";
import { FC } from "react";

const FormSchema = z.object({
  locationKey: z.string({
    required_error: "Please select a language.",
  }),
});

interface LocationSelector {
  locations:
    | {
        id: number;
        locationKey: string;
        locationDescription: string;
      }[]
    | undefined;
}

export const LocationSelector: FC<LocationSelector> = ({ locations }) => {
  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
  });

  function onSubmit(data: z.infer<typeof FormSchema>) {
    console.log(data);
    toast({
      title: "You submitted the following values:",
      description: (
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">{JSON.stringify(data, null, 2)}</code>
        </pre>
      ),
    });
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="locationKey"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel>Location</FormLabel>
              <Popover>
                <PopoverTrigger asChild>
                  <FormControl className="bg-primary">
                    <Button
                      variant="outline"
                      role="combobox"
                      className={cn(
                        "w-[200px] justify-between",
                        !field.value && "text-muted-foreground",
                      )}
                    >
                      {field.value
                        ? locations.find(
                            (location) => location.locationKey === field.value,
                          )?.locationDescription
                        : "Select location"}
                      <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                    </Button>
                  </FormControl>
                </PopoverTrigger>
                <PopoverContent className="w-[200px] p-0">
                  <Command className="bg-primary">
                    <CommandInput
                      className="text-white"
                      placeholder="Search language..."
                    />
                    <CommandEmpty>No location found.</CommandEmpty>
                    <CommandGroup className="text-white">
                      {locations?.map((location) => (
                        <CommandItem
                          value={location.locationDescription}
                          key={location.locationKey}
                          onSelect={() => {
                            form.setValue("locationKey", location.locationKey);
                          }}
                        >
                          <Check
                            className={cn(
                              "mr-2 h-4 w-4",
                              location.locationKey === field.value
                                ? "opacity-100"
                                : "opacity-0",
                            )}
                          />
                          {location.locationDescription}
                        </CommandItem>
                      ))}
                    </CommandGroup>
                  </Command>
                </PopoverContent>
              </Popover>
              <FormDescription>
                Select a location to show the local weather.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button type="submit">Submit</Button>
      </form>
    </Form>
  );
};
